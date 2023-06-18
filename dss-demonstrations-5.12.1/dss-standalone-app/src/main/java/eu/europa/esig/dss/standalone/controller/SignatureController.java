package eu.europa.esig.dss.standalone.controller;

import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.SignatureTokenType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.standalone.enumeration.SignatureOption;
import eu.europa.esig.dss.standalone.fx.CollectionFilesSelectToStringConverter;
import eu.europa.esig.dss.standalone.fx.DSSFileChooser;
import eu.europa.esig.dss.standalone.fx.DSSFileChooserLoader;
import eu.europa.esig.dss.standalone.fx.FileToStringConverter;
import eu.europa.esig.dss.standalone.model.SignatureModel;
import eu.europa.esig.dss.standalone.source.PropertyReader;
import eu.europa.esig.dss.standalone.source.TLValidationJobExecutor;
import eu.europa.esig.dss.standalone.task.SigningTask;
import eu.europa.esig.dss.utils.Utils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class SignatureController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureController.class);
	
	private static final List<DigestAlgorithm> SUPPORTED_DIGEST_ALGORITHMS = Arrays.asList(DigestAlgorithm.SHA1, DigestAlgorithm.SHA224, DigestAlgorithm.SHA256,
			DigestAlgorithm.SHA384, DigestAlgorithm.SHA512, DigestAlgorithm.SHA3_224, DigestAlgorithm.SHA3_256, DigestAlgorithm.SHA3_384, DigestAlgorithm.SHA3_512);
	
	/** A list of DigestAlgorithms supported by the current chosen SignatureFormat */
	private List<DigestAlgorithm> sigFormSupportedDigestAlgorithms;

	/** A list of DigestAlgorithms supported by the current chosen SignatureTokenType */
	private List<DigestAlgorithm> sigTokenTypeSupportedDigestAlgorithms;

	@FXML
	private Button fileSelectButton;

	@FXML
	public RadioButton asicNoneRadio;

	@FXML
	private RadioButton asicsRadio;

	@FXML
	private RadioButton asiceRadio;

	@FXML
	private ToggleGroup toggleAsicContainerType;

	@FXML
	private ToggleGroup toogleSigFormat;

	@FXML
	private ToggleGroup toggleSigPackaging;

	@FXML
	private ToggleGroup toggleSignatureOption;

	@FXML
	private RadioButton cadesRadio;

	@FXML
	private RadioButton padesRadio;

	@FXML
	private RadioButton xadesRadio;

	@FXML
	private RadioButton jadesRadio;

	@FXML
	private HBox hSignaturePackaging;

	@FXML
	private HBox hSignatureOption;
	
	@FXML
	private HBox hBoxDigestAlgos;

	@FXML
	private RadioButton envelopedRadio;

	@FXML
	private RadioButton envelopingRadio;

	@FXML
	private RadioButton detachedRadio;

	@FXML
	private RadioButton internallyDetachedRadio;

	@FXML
	private RadioButton tlSigning;

	@FXML
	private RadioButton xmlManifest;

	@FXML
	private ComboBox<SignatureLevel> comboLevel;

	@FXML
	private Label warningLabel;

	@FXML
	private ToggleGroup toggleDigestAlgo;

	@FXML
	private ToggleGroup toggleSigToken;

	@FXML
	private RadioButton pkcs11Radio;

	@FXML
	private RadioButton pkcs12Radio;
	
	@FXML
	private RadioButton mscapiRadio;

	@FXML
	private HBox hPkcsFile;

	@FXML
	private Label labelPkcs11File;

	@FXML
	private Label labelPkcs12File;

	@FXML
	private HBox hPkcsPassword;

	@FXML
	private Button pkcsFileButton;

	@FXML
	private PasswordField pkcsPassword;

	@FXML
	public Label warningMockTSALabel;

	@FXML
	private Button signButton;

	@FXML
	private ProgressIndicator progressSign;
	
	private ProgressIndicator progressRefreshLOTL;

	private SignatureModel model;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new SignatureModel();
		
		// Allows to collapse items
		hPkcsFile.managedProperty().bind(hPkcsFile.visibleProperty());
		hPkcsPassword.managedProperty().bind(hPkcsPassword.visibleProperty());
		labelPkcs11File.managedProperty().bind(labelPkcs11File.visibleProperty());
		labelPkcs12File.managedProperty().bind(labelPkcs12File.visibleProperty());

		fileSelectButton.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent event) {
				DSSFileChooser fileChooser = DSSFileChooserLoader.getInstance().createFileChooser("File(s) to sign");
				List<File> filesToSign = fileChooser.showOpenMultipleDialog(stage);
				model.setFilesToSign(filesToSign);
				updatePropertiesForm();
			}
		});
		fileSelectButton.textProperty().bindBidirectional(model.filesToSignProperty(), new CollectionFilesSelectToStringConverter());

		asicNoneRadio.setSelected(true);
		asicsRadio.setUserData(ASiCContainerType.ASiC_S);
		asiceRadio.setUserData(ASiCContainerType.ASiC_E);
		toggleAsicContainerType.selectedToggleProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				ASiCContainerType newContainerType = null;
				if (newValue != null) {
					newContainerType = (ASiCContainerType) newValue.getUserData();
				}
				model.setAsicContainerType(newContainerType);
				updatePropertiesForm();
			}
		});

		cadesRadio.setUserData(SignatureForm.CAdES);
		xadesRadio.setUserData(SignatureForm.XAdES);
		padesRadio.setUserData(SignatureForm.PAdES);
		jadesRadio.setUserData(SignatureForm.JAdES);
		toogleSigFormat.selectedToggleProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				SignatureForm newSigForm = null;
				if (newValue != null) {
					newSigForm = (SignatureForm) newValue.getUserData();
				}
				model.setSignatureForm(newSigForm);
				updatePropertiesForm();
			}
		});
		
		envelopedRadio.setUserData(SignaturePackaging.ENVELOPED);
		envelopingRadio.setUserData(SignaturePackaging.ENVELOPING);
		detachedRadio.setUserData(SignaturePackaging.DETACHED);
		internallyDetachedRadio.setUserData(SignaturePackaging.INTERNALLY_DETACHED);
		toggleSigPackaging.selectedToggleProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				SignaturePackaging newPackaging = null;
				if (newValue != null) {
					newPackaging = (SignaturePackaging) newValue.getUserData();
				}
				model.setSignaturePackaging(newPackaging);
				updatePropertiesForm();
			}
		});

		tlSigning.setUserData(SignatureOption.TL_SIGNING);
		xmlManifest.setUserData(SignatureOption.XML_MANIFEST_SIGNING);
		toggleSignatureOption.selectedToggleProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				SignatureOption signatureOption = null;
				if (newValue != null) {
					signatureOption = (SignatureOption) newValue.getUserData();
				}
				model.setSignatureOption(signatureOption);
				updatePropertiesForm();
			}
		});
		
		for (DigestAlgorithm digestAlgo : SUPPORTED_DIGEST_ALGORITHMS) {
			RadioButton rb = new RadioButton(digestAlgo.getName());
			rb.setUserData(digestAlgo);
			rb.setToggleGroup(toggleDigestAlgo);
			hBoxDigestAlgos.getChildren().add(rb);
		}
		
		toggleDigestAlgo.selectedToggleProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue != null) {
					DigestAlgorithm digestAlgorithm = (DigestAlgorithm) newValue.getUserData();
					model.setDigestAlgorithm(digestAlgorithm);
				} else {
					model.setDigestAlgorithm(null);
				}
			}
		});

		comboLevel.valueProperty().bindBidirectional(model.signatureLevelProperty());

		pkcs11Radio.setUserData(SignatureTokenType.PKCS11);
		pkcs12Radio.setUserData(SignatureTokenType.PKCS12);
		mscapiRadio.setUserData(SignatureTokenType.MSCAPI);
		toggleSigToken.selectedToggleProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue != null) {
					SignatureTokenType tokenType = (SignatureTokenType) newValue.getUserData();
					model.setTokenType(tokenType);
				}
				model.setPkcsFile(null);
				model.setPassword(null);

				updatePropertiesForm();
			}
		});
		
		pkcsFileButton.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent event) {
				DSSFileChooser fileChooser;
				switch (model.getTokenType()) {
					case PKCS11:
						fileChooser = DSSFileChooserLoader.getInstance().createFileChooser(
								"Library", "PKCS11 library (*.dll)", "*.dll");
						break;
					case PKCS12:
						fileChooser = DSSFileChooserLoader.getInstance().createFileChooser(
								"Keystore", "PKCS12 keystore (*.p12, *.pfx)", "*.p12", "*.pfx");
						break;
					default:
						throw new UnsupportedOperationException(String.format("Token type '%s' is not supported!", model.getTokenType()));
				}

				File pkcsFile = fileChooser.showOpenDialog(stage);
				model.setPkcsFile(pkcsFile);
			}
		});
		pkcsFileButton.textProperty().bindBidirectional(model.pkcsFileProperty(), new FileToStringConverter());

		pkcsPassword.textProperty().bindBidirectional(model.passwordProperty());

		BooleanBinding isPkcs11Or12 = model.tokenTypeProperty().isEqualTo(SignatureTokenType.PKCS11)
				.or(model.tokenTypeProperty().isEqualTo(SignatureTokenType.PKCS12));

		hPkcsFile.visibleProperty().bind(isPkcs11Or12);
		hPkcsPassword.visibleProperty().bind(isPkcs11Or12);

		labelPkcs11File.visibleProperty().bind(model.tokenTypeProperty().isEqualTo(SignatureTokenType.PKCS11));
		labelPkcs12File.visibleProperty().bind(model.tokenTypeProperty().isEqualTo(SignatureTokenType.PKCS12));

		BooleanBinding isMandatoryFieldsEmpty = model.filesToSignProperty().isNull()
				.or(model.signatureFormProperty().isNull()).or(model.digestAlgorithmProperty().isNull())
				.or(model.tokenTypeProperty().isNull());

		BooleanBinding isPackagingEmpty = model.asicContainerTypeProperty().isNull()
				.and(model.signaturePackagingProperty().isNull());

		BooleanBinding isEmptyFileOrPassword = model.pkcsFileProperty().isNull().or(model.passwordProperty().isEmpty());

		BooleanBinding isPKCSToken = model.tokenTypeProperty().isEqualTo(SignatureTokenType.PKCS11)
				.or(model.tokenTypeProperty().isEqualTo(SignatureTokenType.PKCS12));
		BooleanBinding isPKCSIncomplete = isPKCSToken.and(isEmptyFileOrPassword);

		final BooleanBinding disableSignButton = isMandatoryFieldsEmpty.or(isPackagingEmpty)
				.or(isPKCSIncomplete);

		signButton.disableProperty().bind(disableSignButton);

		signButton.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent event) {
				progressSign.setDisable(false);

				final Service<DSSDocument> service = new Service<>() {
					@Override
					protected Task<DSSDocument> createTask() {
						return new SigningTask(model, TLValidationJobExecutor.getInstance().getCertificateSources());
					}
				};
				service.setOnSucceeded(new EventHandler<>() {
					@Override
					public void handle(WorkerStateEvent event) {
						save(service.getValue());
						signButton.disableProperty().bind(disableSignButton);
						model.setPassword(null);
					}
				});
				service.setOnFailed(new EventHandler<>() {
					@Override
					public void handle(WorkerStateEvent event) {
						String errorMessage = "Oops an error occurred : " + service.getMessage();
						LOG.error(errorMessage, service.getException());
						Alert alert = new Alert(AlertType.ERROR, errorMessage, ButtonType.CLOSE);
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
						signButton.disableProperty().bind(disableSignButton);
						model.setPassword(null);
					}
				});

				progressSign.progressProperty().bind(service.progressProperty());
				signButton.disableProperty().bind(service.runningProperty());
				service.start();
			}
		});

		warningMockTSALabel.setVisible(Utils.isTrue(PropertyReader.getBooleanProperty("timestamp.mock")));
	}

	private void updatePropertiesForm() {
		updateSignatureFormAndPackaging();
		updateSignatureLevelAndDigestAlgo();
		updateSignatureOption();

		reinitDigestAlgos();
	}

	private void updateSignatureFormAndPackaging() {
		if (model.getAsicContainerType() != null) {
			activateRadioButtons(xadesRadio, cadesRadio);
			disableRadioButtons(padesRadio, jadesRadio);
			disableRadioButtons(envelopingRadio, envelopedRadio, detachedRadio, internallyDetachedRadio);
			disableRadioButtons(tlSigning, xmlManifest);

		} else if (Utils.collectionSize(model.getFilesToSign()) > 1) {
			activateRadioButtons(xadesRadio, jadesRadio);
			disableRadioButtons(cadesRadio, padesRadio);
			disableRadioButtons(tlSigning, xmlManifest);
			if (model.getSignatureForm() != null) {
				switch (model.getSignatureForm()) {
					case XAdES:
						activateRadioButtons(envelopingRadio, detachedRadio, internallyDetachedRadio);
						disableRadioButtons(envelopedRadio);
						break;

					case JAdES:
						activateRadioButtons(detachedRadio);
						disableRadioButtons(envelopedRadio, envelopingRadio, internallyDetachedRadio);
						break;

					default:
						break;
				}
			}

		} else {
			activateRadioButtons(xadesRadio, cadesRadio, padesRadio, jadesRadio);
			if (model.getSignatureForm() != null) {
				switch (model.getSignatureForm()) {
					case XAdES:
						if (model.getSignaturePackaging() == null && model.getSignatureOption() != null) {
							switch (model.getSignatureOption()) {
								case TL_SIGNING:
									envelopedRadio.setSelected(true);
									break;
								case XML_MANIFEST_SIGNING:
									envelopingRadio.setSelected(true);
									break;
								default:
									break;
							}
						} else {
							activateRadioButtons(envelopingRadio, envelopedRadio, detachedRadio, internallyDetachedRadio);
						}
						activateRadioButtons(tlSigning, xmlManifest);
						break;

					case CAdES:
					case JAdES:
						activateRadioButtons(envelopingRadio, detachedRadio);
						disableRadioButtons(envelopedRadio, internallyDetachedRadio);
						disableRadioButtons(tlSigning, xmlManifest);
						break;

					case PAdES:
						activateRadioButtons(envelopedRadio);
						disableRadioButtons(envelopingRadio, detachedRadio, internallyDetachedRadio);
						disableRadioButtons(tlSigning, xmlManifest);
						break;

					default:
						break;
				}
			} else {
				reinitSignaturePackagings();
				reinitSignatureOptions();
			}
		}
	}

	private void updateSignatureLevelAndDigestAlgo() {
		sigFormSupportedDigestAlgorithms = new ArrayList<>(SUPPORTED_DIGEST_ALGORITHMS);

		SignatureForm signatureForm = model.getSignatureForm();
		if (signatureForm != null) {
			switch (signatureForm) {
				case XAdES:
					if (model.getSignatureOption() != null) {
						switch (model.getSignatureOption()) {
							case TL_SIGNING:
								updateSignatureLevels(SignatureLevel.XAdES_BASELINE_B);
								sigFormSupportedDigestAlgorithms = Arrays.asList(DigestAlgorithm.SHA256,
										DigestAlgorithm.SHA384, DigestAlgorithm.SHA512);
								break;
							case XML_MANIFEST_SIGNING:
								updateSignatureLevels(SignatureLevel.XAdES_BASELINE_B, SignatureLevel.XAdES_BASELINE_T,
										SignatureLevel.XAdES_BASELINE_LT, SignatureLevel.XAdES_BASELINE_LTA);
								sigFormSupportedDigestAlgorithms = Arrays.asList(DigestAlgorithm.SHA256,
										DigestAlgorithm.SHA384, DigestAlgorithm.SHA512);
								break;
							default:
								break;
						}
					} else {
						updateSignatureLevels(SignatureLevel.XAdES_BASELINE_B, SignatureLevel.XAdES_BASELINE_T,
								SignatureLevel.XAdES_BASELINE_LT, SignatureLevel.XAdES_BASELINE_LTA);
						sigFormSupportedDigestAlgorithms = Arrays.asList(DigestAlgorithm.SHA1, DigestAlgorithm.SHA224, DigestAlgorithm.SHA256,
								DigestAlgorithm.SHA384, DigestAlgorithm.SHA512);
					}
					break;

				case CAdES:
					updateSignatureLevels(SignatureLevel.CAdES_BASELINE_B, SignatureLevel.CAdES_BASELINE_T,
							SignatureLevel.CAdES_BASELINE_LT, SignatureLevel.CAdES_BASELINE_LTA);
					break;

				case PAdES:
					updateSignatureLevels(SignatureLevel.PAdES_BASELINE_B, SignatureLevel.PAdES_BASELINE_T,
							SignatureLevel.PAdES_BASELINE_LT, SignatureLevel.PAdES_BASELINE_LTA);
					break;

				case JAdES:
					sigFormSupportedDigestAlgorithms = Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA384, DigestAlgorithm.SHA512);

					updateSignatureLevels(SignatureLevel.JAdES_BASELINE_B, SignatureLevel.JAdES_BASELINE_T,
							SignatureLevel.JAdES_BASELINE_LT, SignatureLevel.JAdES_BASELINE_LTA);
					break;

				default:
					updateSignatureLevels();
					break;
			}
		}
	}

	private void updateSignatureOption() {
		if (SignatureForm.XAdES.equals(model.getSignatureForm()) && model.getSignaturePackaging() != null
				&& Utils.collectionSize(model.getFilesToSign()) < 2) {
			switch (model.getSignaturePackaging()) {
				case ENVELOPED:
					tlSigning.setDisable(false);
					disableRadioButtons(xmlManifest);
					break;
				case ENVELOPING:
					xmlManifest.setDisable(false);
					disableRadioButtons(tlSigning);
					break;
				default:
					disableRadioButtons(tlSigning, xmlManifest);
					break;
			}
		}
	}

	private void activateRadioButtons(RadioButton... radioButtons) {
		for (RadioButton radioButton : radioButtons) {
			radioButton.setDisable(false);
			if (radioButtons.length == 1) {
				radioButton.setSelected(true);
			}
		}
	}

	private void disableRadioButtons(RadioButton... radioButtons) {
		for (RadioButton radioButton : radioButtons) {
			radioButton.setDisable(true);
			radioButton.setSelected(false);
		}
	}

	private void updateSignatureLevels(SignatureLevel... signatureLevels) {
		comboLevel.setDisable(false);
		comboLevel.getItems().removeAll(comboLevel.getItems());
		if (Utils.isArrayNotEmpty(signatureLevels)) {
			comboLevel.getItems().addAll(signatureLevels);
			comboLevel.setValue(signatureLevels[0]);
		}
	}

	private void reinitSignaturePackagings() {
		disableRadioButtons(envelopingRadio, envelopedRadio, detachedRadio, internallyDetachedRadio);
	}

	private void reinitSignatureOptions() {
		disableRadioButtons(tlSigning, xmlManifest);
	}
	
	private void reinitDigestAlgos() {
		ArrayList<DigestAlgorithm> digestAlgos = new ArrayList<>(SUPPORTED_DIGEST_ALGORITHMS);
		if (sigFormSupportedDigestAlgorithms != null) {
			digestAlgos.retainAll(sigFormSupportedDigestAlgorithms);
		}
		if (sigTokenTypeSupportedDigestAlgorithms != null) {
			digestAlgos.retainAll(sigTokenTypeSupportedDigestAlgorithms);
		}
		if (SignatureTokenType.MSCAPI.equals(model.getTokenType())) {
			digestAlgos.remove(DigestAlgorithm.SHA224);
			digestAlgos.remove(DigestAlgorithm.SHA3_224);
			digestAlgos.remove(DigestAlgorithm.SHA3_256);
			digestAlgos.remove(DigestAlgorithm.SHA3_384);
			digestAlgos.remove(DigestAlgorithm.SHA3_512);
		}
		
		for (Node daButton : hBoxDigestAlgos.getChildren()) {
			DigestAlgorithm digestAlgorithm = (DigestAlgorithm) daButton.getUserData();
			if (digestAlgorithm == null) {
				// nothing chosen case
			} else if (digestAlgos.contains(digestAlgorithm)) {
				daButton.setDisable(false);
			} else {
				daButton.setDisable(true);
				Toggle selectedToggle = toggleDigestAlgo.getSelectedToggle();
				if (selectedToggle != null && digestAlgorithm.equals(selectedToggle.getUserData())) {
					selectedToggle.setSelected(false);
				}
			}
		}
	}

}
