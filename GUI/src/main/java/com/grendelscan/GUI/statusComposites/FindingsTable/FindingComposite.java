package com.grendelscan.GUI.statusComposites.FindingsTable;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GShell;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GrendelScan;
import com.grendelscan.data.findings.Finding;

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
public class FindingComposite extends com.grendelscan.GUI.customControls.basic.GComposite
{

	private final Finding	finding;
	private GLabel			riskLabel;
	private GText			riskText;
	private GLabel			sourceLabel;
	private GText			sourceText;

	private GLabel			titleLabel;

	private GText			titleText;
	private GLabel descriptionLabel;
	private Browser referencesText;
	private GLabel referencesLabel;
	private GLabel urlsLabel;
	private Browser recomendationText;
	private GLabel recomendationLabel;
	private GText urlsText;
	private Browser descriptionText;

	public static void displayFinding(Finding finding) 
	{
		Display display = Display.getDefault();
		GShell shell = new GShell(display);
		Image icon = new Image(display, GrendelScan.defaultConfigDirectory + File.separator + "icon.JPG");
		shell.setImage(icon);
		FindingComposite inst = new FindingComposite(shell, SWT.NULL, finding);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private FindingComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style, Finding finding)
	{
		super(parent, style);
		this.finding = finding;
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			FormLayout thisLayout = new FormLayout();
			setLayout(thisLayout);
			this.setSize(662, 594);
			{
				FormData referencesTextLData = new FormData();
				referencesTextLData.left =  new FormAttachment(0, 1000, 108);
				referencesTextLData.top =  new FormAttachment(0, 1000, 498);
				referencesTextLData.width = 514;
				referencesTextLData.height = 71;
				referencesText = new Browser(this, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
				referencesText.setLayoutData(referencesTextLData);
				referencesText.setText(finding.getReferences());
			}
			{
				referencesLabel = new GLabel(this, SWT.NONE);
				FormData referencesLabelLData = new FormData();
				referencesLabelLData.left =  new FormAttachment(0, 1000, 12);
				referencesLabelLData.top =  new FormAttachment(0, 1000, 498);
				referencesLabelLData.width = 59;
				referencesLabelLData.height = 13;
				referencesLabel.setLayoutData(referencesLabelLData);
				referencesLabel.setText("References:");
			}
			{
				FormData recomendationTextLData = new FormData();
				recomendationTextLData.left =  new FormAttachment(0, 1000, 108);
				recomendationTextLData.top =  new FormAttachment(0, 1000, 357);
				recomendationTextLData.width = 536;
				recomendationTextLData.height = 103;
				recomendationTextLData.right =  new FormAttachment(1000, 1000, -12);
				recomendationText = new Browser(this, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
				recomendationText.setLayoutData(recomendationTextLData);
				recomendationText.setText(finding.getRecomendations());
			}
			{
				recomendationLabel = new GLabel(this, SWT.NONE);
				FormData recomendationLabelLData = new FormData();
				recomendationLabelLData.left =  new FormAttachment(0, 1000, 9);
				recomendationLabelLData.top =  new FormAttachment(0, 1000, 357);
				recomendationLabelLData.width = 83;
				recomendationLabelLData.height = 13;
				recomendationLabel.setLayoutData(recomendationLabelLData);
				recomendationLabel.setText("Recomendations:");
			}
			{
				FormData urlsTextLData = new FormData();
				urlsTextLData.left =  new FormAttachment(0, 1000, 108);
				urlsTextLData.top =  new FormAttachment(0, 1000, 117);
				urlsTextLData.width = 514;
				urlsTextLData.height = 38;
				urlsText = new GText(this, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
				urlsText.setLayoutData(urlsTextLData);
				urlsText.setText(finding.getUrl());
			}
			{
				urlsLabel = new GLabel(this, SWT.NONE);
				FormData urlsLabelLData = new FormData();
				urlsLabelLData.left =  new FormAttachment(0, 1000, 12);
				urlsLabelLData.top =  new FormAttachment(0, 1000, 117);
				urlsLabelLData.width = 43;
				urlsLabelLData.height = 13;
				urlsLabel.setLayoutData(urlsLabelLData);
				urlsLabel.setText("URLs:");
			}
			{
				FormData descriptionTextLData = new FormData();
				descriptionTextLData.left =  new FormAttachment(0, 1000, 108);
				descriptionTextLData.top =  new FormAttachment(0, 1000, 179);
				descriptionTextLData.width = 514;
				descriptionTextLData.height = 154;
				descriptionTextLData.right =  new FormAttachment(1000, 1000, -12);
				descriptionText = new Browser(this, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
				descriptionText.setLayoutData(descriptionTextLData);
				descriptionText.setText(finding.getLongDescription() + finding.getLongDescriptionFooter());
			}
			{
				descriptionLabel = new GLabel(this, SWT.NONE);
				FormData descriptionLabelLData = new FormData();
				descriptionLabelLData.left =  new FormAttachment(0, 1000, 12);
				descriptionLabelLData.top =  new FormAttachment(0, 1000, 179);
				descriptionLabelLData.width = 57;
				descriptionLabelLData.height = 13;
				descriptionLabel.setLayoutData(descriptionLabelLData);
				descriptionLabel.setText("Description:");
			}
			{
				sourceText = new GText(this, SWT.READ_ONLY | SWT.BORDER);
				FormData sourceTextLData = new FormData();
				sourceTextLData.left =  new FormAttachment(0, 1000, 108);
				sourceTextLData.top =  new FormAttachment(0, 1000, 80);
				sourceTextLData.width = 230;
				sourceTextLData.height = 13;
				sourceText.setLayoutData(sourceTextLData);
				sourceText.setText(finding.getSource());
			}
			{
				FormData riskTextLData = new FormData();
				riskTextLData.left =  new FormAttachment(0, 1000, 107);
				riskTextLData.top =  new FormAttachment(0, 1000, 47);
				riskTextLData.width = 64;
				riskTextLData.height = 13;
				riskText = new GText(this, SWT.READ_ONLY | SWT.BORDER);
				riskText.setLayoutData(riskTextLData);
				riskText.setText(finding.getSeverity().getFullName());
			}
			{
				FormData titleTextLData = new FormData();
				titleTextLData.left =  new FormAttachment(0, 1000, 107);
				titleTextLData.top =  new FormAttachment(0, 1000, 13);
				titleTextLData.width = 185;
				titleTextLData.height = 13;
				titleText = new GText(this, SWT.READ_ONLY | SWT.BORDER);
				titleText.setLayoutData(titleTextLData);
				titleText.setText(finding.getTitle());
			}
			{
				titleLabel = new GLabel(this, SWT.NONE);
				FormData titleLabelLData = new FormData();
				titleLabelLData.left = new FormAttachment(0, 1000, 12);
				titleLabelLData.top = new FormAttachment(0, 1000, 13);
				titleLabelLData.width = 24;
				titleLabelLData.height = 13;
				titleLabel.setLayoutData(titleLabelLData);
				titleLabel.setText("Title:");
			}
			{
				sourceLabel = new GLabel(this, SWT.NONE);
				FormData sourceLabelLData = new FormData();
				sourceLabelLData.left = new FormAttachment(0, 1000, 12);
				sourceLabelLData.top = new FormAttachment(0, 1000, 80);
				sourceLabelLData.width = 43;
				sourceLabelLData.height = 13;
				sourceLabel.setLayoutData(sourceLabelLData);
				sourceLabel.setText("Source:");
			}
			{
				riskLabel = new GLabel(this, SWT.NONE);
				FormData riskLabelLData = new FormData();
				riskLabelLData.left = new FormAttachment(0, 1000, 12);
				riskLabelLData.top = new FormAttachment(0, 1000, 47);
				riskLabelLData.width = 23;
				riskLabelLData.height = 13;
				riskLabel.setLayoutData(riskLabelLData);
				riskLabel.setText("Risk:");
			}
			this.layout();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Overriding checkSubclass allows this class to extend
	 * com.grendelscan.GUI.customControls.basic.GComposite
	 */
	@Override
	protected void checkSubclass()
	{
	}

}
