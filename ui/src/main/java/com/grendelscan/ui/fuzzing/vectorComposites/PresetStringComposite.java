package com.grendelscan.ui.fuzzing.vectorComposites;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.FileDialog;

import com.grendelscan.commons.FileUtils;
import com.grendelscan.fuzzing.FuzzVector;
import com.grendelscan.fuzzing.PredefinedList;
import com.grendelscan.ui.customControls.AddRemoveListComposite;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GText;
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
public class PresetStringComposite extends com.grendelscan.ui.customControls.basic.GComposite implements FuzzVectorComposite
{
	private GGroup fileImportGroup;
	private GText seperatorStringTextbox;
	private GButton importFromFileButton;
	GButton newLineCheckbox;
	private GLabel seperatorStringLabel;
	private AddRemoveListComposite list;

	public PresetStringComposite(com.grendelscan.ui.customControls.basic.GComposite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			this.setLayout(new FormLayout());
			{
				fileImportGroup = new GGroup(this, SWT.NONE);
				FormLayout fileImportGroupLayout = new FormLayout();
				fileImportGroup.setLayout(fileImportGroupLayout);
				FormData fileImportGroupLData = new FormData();
				fileImportGroupLData.width = 251;
				fileImportGroupLData.height = 107;
				fileImportGroupLData.left =  new FormAttachment(0, 1000, 0);
				fileImportGroupLData.top =  new FormAttachment(0, 1000, 125);
				fileImportGroup.setLayoutData(fileImportGroupLData);
				fileImportGroup.setText("Import From File");
				{
					newLineCheckbox = new GButton(fileImportGroup, SWT.CHECK | SWT.LEFT);
					FormData newLineCheckboxLData = new FormData();
					newLineCheckboxLData.width = 172;
					newLineCheckboxLData.height = 19;
					newLineCheckboxLData.left =  new FormAttachment(0, 1000, 9);
					newLineCheckboxLData.top =  new FormAttachment(0, 1000, 7);
					newLineCheckbox.setLayoutData(newLineCheckboxLData);
					newLineCheckbox.setText("Seperate with new lines");
					newLineCheckbox.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) 
						{
							seperatorStringTextbox.setEnabled(!newLineCheckbox.getSelection());
							seperatorStringLabel.setEnabled(!newLineCheckbox.getSelection());
						}
					});
				}
				{
					FormData seperatorStringTextboxLData = new FormData();
					seperatorStringTextboxLData.width = 91;
					seperatorStringTextboxLData.height = 19;
					seperatorStringTextboxLData.left =  new FormAttachment(0, 1000, 127);
					seperatorStringTextboxLData.top =  new FormAttachment(0, 1000, 32);
					seperatorStringTextbox = new GText(fileImportGroup, SWT.BORDER);
					seperatorStringTextbox.setLayoutData(seperatorStringTextboxLData);
				}
				{
					seperatorStringLabel = new GLabel(fileImportGroup, SWT.NONE);
					FormData seperatorStringLabelLData = new FormData();
					seperatorStringLabelLData.width = 112;
					seperatorStringLabelLData.height = 17;
					seperatorStringLabelLData.left =  new FormAttachment(0, 1000, 9);
					seperatorStringLabelLData.top =  new FormAttachment(0, 1000, 32);
					seperatorStringLabel.setLayoutData(seperatorStringLabelLData);
					seperatorStringLabel.setText("Seperator string:");
				}
				{
					importFromFileButton = new GButton(fileImportGroup, SWT.PUSH | SWT.CENTER);
					FormData importFromFileButtonLData = new FormData();
					importFromFileButtonLData.width = 73;
					importFromFileButtonLData.height = 27;
					importFromFileButtonLData.left =  new FormAttachment(0, 1000, 9);
					importFromFileButtonLData.top =  new FormAttachment(0, 1000, 61);
					importFromFileButton.setLayoutData(importFromFileButtonLData);
					importFromFileButton.setText("Browse");
					importFromFileButton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							importFromFileButtonWidgetSelected(evt);
						}
					});
				}
				{
					FormData listLData = new FormData();
					listLData.width = 525;
					listLData.height = 113;
					listLData.left =  new FormAttachment(0, 1000, 0);
					listLData.top =  new FormAttachment(0, 1000, 0);
					listLData.right =  new FormAttachment(1000, 1000, 0);
					list = new AddRemoveListComposite(this, SWT.NONE, "", null, null);
					list.setLayoutData(listLData);
				}
			}
			this.layout();
	}
	
	private void importFromFileButtonWidgetSelected(SelectionEvent evt) 
	{
		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		String filename = fd.open();
		if (filename != null && !filename.equals(""))
		{
			String contents = FileUtils.readFile(filename);
			
			String regex;
			if (newLineCheckbox.getSelection())
			{
				regex = "\r?\n";
			}
			else
			{
				regex = Pattern.quote(seperatorStringTextbox.getText());
			}
			
			String strings[] = contents.split(regex);
			
			Set<String> stringSet = new HashSet<String>(strings.length);
			for (String string: strings)
			{
				stringSet.add(string);
			}
			list.addItems(stringSet.toArray(new String[0]));
		}
	}

	@Override
	public FuzzVector getFuzzVector()
    {
		return new PredefinedList(list.getListText());
		
    }

	@Override
	public void displayFuzzVector(FuzzVector oldVector)
    {
		PredefinedList vector = (PredefinedList) oldVector;
		list.addItems(vector.getStrings());
    }

	@Override
	public String getDescription()
    {
	    return "Preset strings";
    }

	@Override
	public Class getFuzzVectorClass()
    {
	    return PredefinedList.class;
    }

}
