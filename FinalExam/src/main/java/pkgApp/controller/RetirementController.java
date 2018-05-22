package pkgApp.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.sun.prism.paint.Color;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import javafx.beans.value.*;

import pkgApp.RetirementApp;
import pkgCore.Retirement;

public class RetirementController implements Initializable {

	private RetirementApp mainApp = null;
	@FXML
	private TextField txtSaveEachMonth;
	@FXML
	private TextField txtYearsToWork;
	@FXML
	private TextField txtAnnualReturnWorking;
	@FXML
	private TextField txtWhatYouNeedToSave;
	@FXML
	private TextField txtYearsRetired;
	@FXML
	private TextField txtAnnualReturnRetired;
	@FXML
	private TextField txtRequiredIncome;
	@FXML
	private TextField txtMonthlySSI;

	private HashMap<TextField, String> hmTextFieldRegEx = new HashMap<TextField, String>();

	public RetirementApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(RetirementApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Adding an entry in the hashmap for each TextField control I want to validate
		// with a regular expression
		// "\\d*?" - means any decimal number
		// "\\d*(\\.\\d*)?" means any decimal, then optionally a period (.), then
		// decmial
		
		hmTextFieldRegEx.put(txtYearsToWork, "(?<!-)\\b([1-3]?\\d|40)\\b\\d*?");
		hmTextFieldRegEx.put(txtAnnualReturnWorking, "\\b(0.0|0.01|0.02|0.03|0.04|0.05|0.06|0.07|0.08|0.09|0.1)\\b");
		hmTextFieldRegEx.put(txtYearsRetired, "(?<!-)\\b([1]?\\d|20)\\b\\d*?");
		hmTextFieldRegEx.put(txtAnnualReturnRetired, "\\d*(\\.\\d*)?");
		hmTextFieldRegEx.put(txtRequiredIncome, "\\d*?");
		hmTextFieldRegEx.put(txtMonthlySSI, "\\d*?");

		// Check out these pages (how to validate controls):
		// https://stackoverflow.com/questions/30935279/javafx-input-validation-textfield
		// https://stackoverflow.com/questions/40485521/javafx-textfield-validation-decimal-value?rq=1
		// https://stackoverflow.com/questions/8381374/how-to-implement-a-numberfield-in-javafx-2-0
		// There are some examples on how to validate / check format

		Iterator it = hmTextFieldRegEx.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			TextField txtField = (TextField) pair.getKey();
			String strRegEx = (String) pair.getValue();

			txtField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
						Boolean newPropertyValue) {
					// If newPropertyValue = true, then the field HAS FOCUS
					// If newPropertyValue = false, then field HAS LOST FOCUS
					if (!newPropertyValue) {
						if (!txtField.getText().matches(strRegEx)) {
							txtField.setText("");
							txtField.requestFocus();
						}
					}
				}
			});
		}

		//
		// TODO: Validate Working Annual Return %, accept only numbers and decimals
		// TODO: Validate Years retired, accepted only decimals
		// TODO: Validate Retired Annual Return %, accept only numbers and deciamls
		// TODO: Validate Required Income, accept only decimals
		// TODO: Validate Monthly SSI, accept only decimals
	}

	@FXML
	public void btnClear(ActionEvent event) {
		System.out.println("Clear Pressed");

		// disable read-only controls
		txtSaveEachMonth.setDisable(true);
		txtWhatYouNeedToSave.setDisable(true);

		// Clear, enable txtYearsToWork
		txtYearsToWork.clear();
		txtYearsToWork.setDisable(false);
		
		txtAnnualReturnWorking.clear();
		txtAnnualReturnWorking.setDisable(false);
		
		txtAnnualReturnRetired.clear();
		txtAnnualReturnRetired.setDisable(false);
		
		txtYearsRetired.clear();
		txtYearsRetired.setDisable(false);
		
		txtRequiredIncome.clear();
		txtRequiredIncome.setDisable(false);
		
		txtMonthlySSI.clear();
		txtMonthlySSI.setDisable(false);

		// TODO: Clear, enable the rest of the input controls. Hint! You already have a
		// HashMap of all the input controls....!!!!
	}

	@FXML
	public void btnCalculate() {

		System.out.println("Calculating");

		txtSaveEachMonth.setDisable(false);
		txtWhatYouNeedToSave.setDisable(false);
		
		Retirement r = new Retirement(Integer.parseInt(txtYearsToWork.getText()),Double.parseDouble(txtAnnualReturnWorking.getText()), Integer.parseInt(txtYearsRetired.getText()), Double.parseDouble(txtAnnualReturnRetired.getText()), Double.parseDouble(txtRequiredIncome.getText()), Double.parseDouble(txtMonthlySSI.getText()));
		
		double PrsntVal=Math.round((Retirement.PV(r.getdAnnualReturnRetired()/12, r.getiYearsRetired()*12, r.getdRequiredIncome()-r.getdMonthlySSI(), 0, false)*(-1)));
		txtWhatYouNeedToSave.setText(String.valueOf(PrsntVal));
		
		double PmtVal=Math.round((Retirement.PMT(r.getdAnnualReturnWorking()/12,r.getiYearsToWork()*12,0,PrsntVal,false)*(-1)));
		txtSaveEachMonth.setText(String.valueOf(PmtVal));
		// TODO: Calculate txtWhatYouNeedToSave value...
		// TODO: Then calculate txtSaveEachMonth, using amount from txtWhatYouNeedToSave
		// as input
	}
}
