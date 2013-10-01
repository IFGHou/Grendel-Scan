package com.grendelscan.GUI.fuzzing.vectorComposites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GUI.Verifiers.EnforceDecimalNumbersOnly;
import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.fuzzing.FuzzVector;
import com.grendelscan.fuzzing.NumericSequence;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class NumericSequenceComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements FuzzVectorComposite 
{
	private GLabel beginLabel;
	private GLabel decimalPlacesLabel;
	private GText decimalPlacesTextBox;
	private Combo ascendingCombo;
	private GText incrementTextBox;
	private GLabel incrementLabel;
	private GText endTextBox;
	private GText beginTextBox;
	private GLabel endLabel;


	public NumericSequenceComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			EnforceDecimalNumbersOnly decimalNumberOnly = new EnforceDecimalNumbersOnly();  
			EnforceIntegersOnly integersOnly = new EnforceIntegersOnly();  
			this.setLayout(new FormLayout());
			{
				decimalPlacesLabel = new GLabel(this, SWT.WRAP);
				FormData decimalPlacesLabelLData = new FormData();
				decimalPlacesLabelLData.width = 55;
				decimalPlacesLabelLData.height = 41;
				decimalPlacesLabelLData.left =  new FormAttachment(0, 1000, 195);
				decimalPlacesLabelLData.top =  new FormAttachment(0, 1000, 39);
				decimalPlacesLabel.setLayoutData(decimalPlacesLabelLData);
				decimalPlacesLabel.setText("Decimal Places:");
			}
			{
				incrementLabel = new GLabel(this, SWT.NONE);
				FormData incrementLabelLData = new FormData();
				incrementLabelLData.width = 66;
				incrementLabelLData.height = 17;
				incrementLabelLData.left =  new FormAttachment(0, 1000, 0);
				incrementLabelLData.top =  new FormAttachment(0, 1000, 39);
				incrementLabel.setLayoutData(incrementLabelLData);
				incrementLabel.setText("Increment:");
			}
			{
				endLabel = new GLabel(this, SWT.NONE);
				FormData endLabelLData = new FormData();
				endLabelLData.width = 29;
				endLabelLData.height = 17;
				endLabelLData.left =  new FormAttachment(0, 1000, 195);
				endLabelLData.top =  new FormAttachment(0, 1000, 3);
				endLabel.setLayoutData(endLabelLData);
				endLabel.setText("End:");
			}
			{
				beginLabel = new GLabel(this, SWT.NONE);
				FormData beginLabelLData = new FormData();
				beginLabelLData.width = 44;
				beginLabelLData.height = 17;
				beginLabelLData.left =  new FormAttachment(0, 1000, 0);
				beginLabelLData.top =  new FormAttachment(0, 1000, 3);
				beginLabel.setLayoutData(beginLabelLData);
				beginLabel.setText("Begin:");
			}
			{
				FormData beginTextBoxLData = new FormData();
				beginTextBoxLData.width = 74;
				beginTextBoxLData.height = 19;
				beginTextBoxLData.left =  new FormAttachment(0, 1000, 74);
				beginTextBoxLData.top =  new FormAttachment(0, 1000, 0);
				beginTextBox = new GText(this, SWT.BORDER);
				beginTextBox.setLayoutData(beginTextBoxLData);
				beginTextBox.addVerifyListener(decimalNumberOnly);
			}
			{
				FormData endTextBoxLData = new FormData();
				endTextBoxLData.width = 74;
				endTextBoxLData.height = 19;
				endTextBoxLData.left =  new FormAttachment(0, 1000, 254);
				endTextBoxLData.top =  new FormAttachment(0, 1000, 0);
				endTextBox = new GText(this, SWT.BORDER);
				endTextBox.setLayoutData(endTextBoxLData);
				endTextBox.addVerifyListener(decimalNumberOnly);
			}
			{
				FormData incrementTextBoxLData = new FormData();
				incrementTextBoxLData.width = 74;
				incrementTextBoxLData.height = 19;
				incrementTextBoxLData.left =  new FormAttachment(0, 1000, 74);
				incrementTextBoxLData.top =  new FormAttachment(0, 1000, 41);
				incrementTextBox = new GText(this, SWT.BORDER);
				incrementTextBox.setLayoutData(incrementTextBoxLData);
				incrementTextBox.addVerifyListener(decimalNumberOnly);
			}
			{
				FormData decimalPlacesTextBoxLData = new FormData();
				decimalPlacesTextBoxLData.width = 74;
				decimalPlacesTextBoxLData.height = 19;
				decimalPlacesTextBoxLData.left =  new FormAttachment(0, 1000, 254);
				decimalPlacesTextBoxLData.top =  new FormAttachment(0, 1000, 43);
				decimalPlacesTextBox = new GText(this, SWT.BORDER);
				decimalPlacesTextBox.setLayoutData(decimalPlacesTextBoxLData);
				decimalPlacesTextBox.setText("0");
				decimalPlacesTextBox.addVerifyListener(integersOnly);
			}
			{
				FormData ascendingComboLData = new FormData();
				ascendingComboLData.width = 83;
				ascendingComboLData.height = 25;
				ascendingComboLData.left =  new FormAttachment(0, 1000, 74);
				ascendingComboLData.top =  new FormAttachment(0, 1000, 80);
				ascendingCombo = new Combo(this, SWT.READ_ONLY);
				ascendingCombo.setLayoutData(ascendingComboLData);
				ascendingCombo.setItems(new String[] {ASCENDING, DESCENDING});
				ascendingCombo.setText(ASCENDING);
			}
			this.layout();
	}

	private static final String ASCENDING = "Ascending";
	private static final String DESCENDING = "Descending";
	@Override
	public FuzzVector getFuzzVector()
    {
	    return new NumericSequence(ascendingCombo.getText().equals(ASCENDING) ? true : false, 
	    		Double.valueOf(incrementTextBox.getText()), 
	    		Double.valueOf(beginTextBox.getText()), 
	    		Double.valueOf(endTextBox.getText()), 
	    		Integer.valueOf(decimalPlacesTextBox.getText()));
    }

	@Override
	public void displayFuzzVector(FuzzVector oldVector)
    {
		NumericSequence vector = (NumericSequence) oldVector;
		beginTextBox.setText(String.valueOf(vector.getBegin()));
		endTextBox.setText(String.valueOf(vector.getEnd()));
		incrementTextBox.setText(String.valueOf(vector.getIncrement()));
		decimalPlacesTextBox.setText(String.valueOf(vector.getDecimalLocations()));
		ascendingCombo.setText(vector.isAscending() ? ASCENDING : DESCENDING);
    }

	@Override
	public String getDescription()
    {
	    return "Numeric sequence";
    }

	@Override
	public Class getFuzzVectorClass()
    {
	    return NumericSequence.class;
    }

}
