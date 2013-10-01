package com.grendelscan.GUI.http.transactionDisplay;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.GUI.customControls.basic.GButton;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.AmfViewComposite;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.BrowserComposite;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.HexViewComposite;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.ParsedEntityComposite;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.RawBodyText;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.UrlEncodedViewComposite;
import com.grendelscan.logging.Log;
import com.grendelscan.utils.StringUtils;


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
public class ParsedBodyComposite extends com.grendelscan.GUI.customControls.basic.GComposite {
	protected GButton hexEncodedRadioButton;
	protected GButton rawEncodedRadio;
	protected GButton selectedButton;
	
	protected RawBodyText rawBodyTextBox;
	protected HexViewComposite hexViewComposite;

	protected ParsedEntityComposite activeControl;
	
	protected ArrayList<ParsedEntityComposite> optionalParsedComposites;
	protected ArrayList<GButton> optionalParsingButtons;
	
	protected HttpContentTypeCategory contentCategory;
	protected String mimeType;
	protected byte[] body;
	protected boolean editable;

	protected FormData bodyFormatData;
	
	static View view = View.CUSTOM;
	
	private enum View { RAW, HEX, CUSTOM}

	public ParsedBodyComposite(GComposite parent, int style, boolean editable) 
	{
		super(parent, style);
		this.editable = editable;
		optionalParsedComposites = new ArrayList<ParsedEntityComposite>(1);
		optionalParsingButtons = new ArrayList<GButton>(1);
		body = new byte[0];
		initGUI();
		restoreView();
	}
	
	private void restoreView()
	{
		switch (view)
		{
			case CUSTOM:
				if (optionalParsedComposites.size() > 0)
				{
					switchToParsed();
					break;
				}
				//$FALL-THROUGH$
			case HEX:
				changeButtonSelection(hexEncodedRadioButton, false);
				break;
			case RAW: 
				changeButtonSelection(rawEncodedRadio, false);
				break;
		}
	}
	
	private void saveView()
	{
		if (selectedButton == hexEncodedRadioButton)
		{
			view = View.HEX;
		}
		else if (selectedButton == rawEncodedRadio)
		{
			view = View.RAW;
		}
		else
		{
			view = View.CUSTOM;
		}
	}
	
	public void switchToParsed()
	{
		if (optionalParsingButtons.size() > 0)
		{
			changeButtonSelection(optionalParsingButtons.get(0), false);
		}
		else
		{
			changeButtonSelection(hexEncodedRadioButton, false);
		}
	}
	
	protected void updateData(@SuppressWarnings("hiding") byte[] body, @SuppressWarnings("hiding") String mimeType)
	{
		if (!this.body.equals(body) || this.mimeType != mimeType)
		{
			this.mimeType = mimeType;
			this.contentCategory = HttpContentTypeCategory.getContentType(mimeType);
			this.body = body;
			updateEncodingOptions();
			
			activeControl.updateData(body);
		}
	}
	
	protected void hideAllViews()
	{
		for (ParsedEntityComposite composite: optionalParsedComposites)
		{
			if (!composite.getWidget().isDisposed())
				composite.setVisible(false);
		}
		hexViewComposite.setVisible(false);
		rawBodyTextBox.setVisible(false);
	}
	
	
	protected void updateBodyControl() throws UpdateCanceledException
	{
		// This needs to be moved to RawBodyTextBox
		if (rawEncodedRadio.getSelection()) 
		{
			String bodyString = new String(body, StringUtils.getDefaultCharset());
			bodyString = ParsedTransactionUtils.PrepareRawString(bodyString, mimeType, editable);
			if (bodyString != null)
			{
				rawBodyTextBox.updateData(bodyString.getBytes());
			}
		}
		else
		{
			ParsedEntityComposite composite = (ParsedEntityComposite) selectedButton.getData();
			composite.updateData(body);
		}
		this.layout();
	}
	
	
	protected void changeButtonSelection(GButton button, boolean useActiveControl)
	{
		GButton oldButton = selectedButton;
		byte[] oldBody = body.clone();
		if (selectedButton != null && ! selectedButton.isDisposed())
		{
			if (editable && useActiveControl)
			{
				body = activeControl.getBytes();
			}
			ParsedEntityComposite oldComposite = (ParsedEntityComposite) selectedButton.getData();
			oldComposite.setVisible(false);
			selectedButton.setSelection(false);
		}
		ParsedEntityComposite control = (ParsedEntityComposite) button.getData();
		activeControl = control;
		control.setVisible(true);
		button.setSelection(true);
		selectedButton = button;
		try
        {
	        updateBodyControl();
        }
        catch (UpdateCanceledException e)
        {
        	if (oldButton != null && !oldButton.isDisposed())
        	{
        		body = oldBody;
            	selectedButton.setSelection(false);
            	selectedButton = oldButton;
        		ParsedEntityComposite oldControl = (ParsedEntityComposite) oldButton.getData();
	        	oldButton.setSelection(true);
	        	control.setVisible(false);
	        	oldControl.setVisible(true);
	        	activeControl = oldControl;
        	}
        	else
        	{
        		Log.warn("Not sure how to handle a null button here");
        	}
        }
	}
	
	protected void processButtonSelection(SelectionEvent event)
	{
		boolean selected = ((GButton)event.getSource()).getSelection();
		if (selected) 
		{
			changeButtonSelection((GButton) event.widget, true);
		}
	}
	
	protected void updateEncodingOptions()
	{
		saveView();
		for (GButton button: optionalParsingButtons)
		{
			disposeWidget(button);
		}
		optionalParsingButtons.clear();
		
		for (ParsedEntityComposite composite: optionalParsedComposites)
		{
			disposeWidget(composite.getWidget());
		}
		optionalParsedComposites.clear();
		
		SelectionAdapter buttonClicked = new SelectionAdapter()
		{
			@Override
            public void widgetSelected(SelectionEvent event)
			{ 
				processButtonSelection(event);
			}
		};

		
		int currentLeft = 180;
		{
			if (contentCategory == HttpContentTypeCategory.URL_ENCODED)
			{
				{
					UrlEncodedViewComposite queryParameterList = new UrlEncodedViewComposite(this, SWT.NONE, 200, editable);
					queryParameterList.setLayoutData(bodyFormatData);
					optionalParsedComposites.add(queryParameterList);


					GButton urlEncodedRadioButton = new GButton(this, SWT.RADIO | SWT.LEFT);
					optionalParsingButtons.add(urlEncodedRadioButton);
					urlEncodedRadioButton.setData(queryParameterList);

					
					FormData urlEncodedRadioButtonLData = new FormData();
					urlEncodedRadioButtonLData.width = 114;
					urlEncodedRadioButtonLData.height = 15;
					urlEncodedRadioButtonLData.left =  new FormAttachment(0, 1000, currentLeft);
					urlEncodedRadioButtonLData.top =  new FormAttachment(0, 1000, 0);
					urlEncodedRadioButton.setLayoutData(urlEncodedRadioButtonLData);
					urlEncodedRadioButton.setText("URL encoded");
					urlEncodedRadioButton.addSelectionListener(buttonClicked);
					
					changeButtonSelection(urlEncodedRadioButton, false);

				}
				currentLeft += 194;
			}
		}
		{
//			if (contentType.equalsIgnoreCase("multipart/form-data"))
//			{
//				multiPartRadioButton = new GButton(this, SWT.RADIO | SWT.LEFT);
//				FormData multiPartRadioButtonLData = new FormData();
//				multiPartRadioButtonLData.width = 150;
//				multiPartRadioButtonLData.height = 15;
//				multiPartRadioButtonLData.left =  new FormAttachment(0, 1000, currentLeft);
//				multiPartRadioButtonLData.top =  new FormAttachment(0, 1000, 0);
//				multiPartRadioButton.setLayoutData(multiPartRadioButtonLData);
//				multiPartRadioButton.setText("Multipart MIME encoded");
//				multiPartRadioButton.addSelectionListener
//				(
//					new SelectionAdapter()
//					{
//						public void widgetSelected(SelectionEvent event)
//						{ 
//							processButtonSelection(event);
//						}
//					}
//				);
//				currentLeft += 230;
//			}
		}
		{
			if (contentCategory == HttpContentTypeCategory.AMF)
			{
				AmfViewComposite amfViewComposite = new AmfViewComposite(this, SWT.NONE, editable);
				amfViewComposite.setLayoutData(bodyFormatData);
				optionalParsedComposites.add(amfViewComposite);
				
				
				GButton amfEncodedRadioButton = new GButton(this, SWT.RADIO | SWT.LEFT);
				optionalParsingButtons.add(amfEncodedRadioButton);
				amfEncodedRadioButton.setData(amfViewComposite);
				
				FormData amfEncodedRadioButtonLData = new FormData();
				amfEncodedRadioButtonLData.width = 100;
				amfEncodedRadioButtonLData.height = 15;
				amfEncodedRadioButtonLData.left =  new FormAttachment(0, 1000, currentLeft);
				amfEncodedRadioButtonLData.top =  new FormAttachment(0, 1000, 0);
				amfEncodedRadioButton.setLayoutData(amfEncodedRadioButtonLData);
				amfEncodedRadioButton.setText("AMF encoded");
				amfEncodedRadioButton.addSelectionListener(buttonClicked);
				currentLeft += 180;
				
				changeButtonSelection(amfEncodedRadioButton, false);
			}
		}
		
		if (contentCategory == HttpContentTypeCategory.HTML)
		{
			BrowserComposite browser = new BrowserComposite(this, SWT.BORDER);
			browser.setLayoutData(bodyFormatData);
			browser.setVisible(false);
			optionalParsedComposites.add(browser);
			
			GButton htmlEncodedRadioButton = new GButton(this, SWT.RADIO | SWT.LEFT);
			optionalParsingButtons.add(htmlEncodedRadioButton);
			htmlEncodedRadioButton.setData(browser);
			
			FormData htmlEncodedRadioButtonLData = new FormData();
			htmlEncodedRadioButtonLData.width = 65;
			htmlEncodedRadioButtonLData.height = 15;
			htmlEncodedRadioButtonLData.left =  new FormAttachment(0, 1000, currentLeft);
			htmlEncodedRadioButtonLData.top =  new FormAttachment(0, 1000, 0);
			currentLeft += htmlEncodedRadioButtonLData.width + 10;
			htmlEncodedRadioButton.setLayoutData(htmlEncodedRadioButtonLData);
			htmlEncodedRadioButton.setText("HTML");
			htmlEncodedRadioButton.addSelectionListener(buttonClicked);
		}

		
		
//		if (contentType == HttpResponseContentType.IMAGE)
//		{
//			imageEncodedRadio = new GButton(this, SWT.RADIO | SWT.LEFT);
//			FormData imageEncodedRadioButtonLData = new FormData();
//			imageEncodedRadioButtonLData.width = 114;
//			imageEncodedRadioButtonLData.height = 15;
//			imageEncodedRadioButtonLData.left =  new FormAttachment(0, 1000, currentLeft);
//			imageEncodedRadioButtonLData.top =  new FormAttachment(0, 1000, 0);
//			currentLeft += imageEncodedRadioButtonLData.width + 10;
//			imageEncodedRadio.setLayoutData(imageEncodedRadioButtonLData);
//			imageEncodedRadio.setText("Image");
//			imageEncodedRadio.addSelectionListener
//			(
//				new SelectionAdapter()
//				{
//					public void widgetSelected(SelectionEvent event)
//					{ 
//						if (needBodyControlUpdate())
//						{
//							updateBody();
//							updateBodyControl();
//						}
//					}
//				}
//			);
//		}
		
		if (activeControl == null)
		{
			changeButtonSelection(rawEncodedRadio, false);
		}

		this.layout();
		if (view == View.RAW && editable && (new String(body)).indexOf('\0') >= 0)
		{
			view = View.CUSTOM;
		}
		restoreView();
		
	}

	protected void disposeWidget(Widget target)
	{
		if (target != null)
		{
			target.dispose();
		}
	}
	
	protected void initGUI() 
	{
		addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent arg0)
			{
				saveView();
			}
		});
		this.setLayout(new FormLayout());
		int currentLeft = 0;
		{
			
			{
				bodyFormatData = new FormData();
				bodyFormatData.width = 828;
				bodyFormatData.height = 374;
				bodyFormatData.left =  new FormAttachment(0, 1000, 0);
				bodyFormatData.top =  new FormAttachment(0, 1000, 16);
				bodyFormatData.bottom =  new FormAttachment(1000, 1000, 0);
				bodyFormatData.right =  new FormAttachment(1000, 1000, 0);
				{
					rawBodyTextBox = new RawBodyText(this, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
					rawBodyTextBox.setLayoutData(bodyFormatData);
					rawBodyTextBox.setVisible(false);
					rawBodyTextBox.setEditable(editable);
				}
				{
					hexViewComposite = new HexViewComposite(this, SWT.BORDER, editable);
					hexViewComposite.setLayoutData(bodyFormatData);
					hexViewComposite.setVisible(false);
				}
			}

			{
				hexEncodedRadioButton = new GButton(this, SWT.RADIO | SWT.LEFT);
				FormData hexEncodedRadioButtonLData = new FormData();
				hexEncodedRadioButton.setData(hexViewComposite);
				
				hexEncodedRadioButtonLData.width = 110;
				hexEncodedRadioButtonLData.height = 15;
				hexEncodedRadioButtonLData.left =  new FormAttachment(0, 1000, currentLeft);
				hexEncodedRadioButtonLData.top =  new FormAttachment(0, 1000, 0);
				hexEncodedRadioButton.setLayoutData(hexEncodedRadioButtonLData);
				hexEncodedRadioButton.setText("Hex encoded");
				hexEncodedRadioButton.addSelectionListener
				(
					new SelectionAdapter()
					{
						@Override
                        public void widgetSelected(SelectionEvent event)
						{ 
							processButtonSelection(event);
						}
					}
				);
				currentLeft += 120;
			}
			{
				rawEncodedRadio = new GButton(this, SWT.RADIO | SWT.LEFT);
				rawEncodedRadio.setData(rawBodyTextBox);

				FormData rawEncodedRadioLData = new FormData();
				rawEncodedRadioLData.width = 50;
				rawEncodedRadioLData.height = 15;
				rawEncodedRadioLData.left =  new FormAttachment(0, 1000, currentLeft);
				rawEncodedRadioLData.top =  new FormAttachment(0, 1000, 0);
				rawEncodedRadio.setLayoutData(rawEncodedRadioLData);
				rawEncodedRadio.setText("Raw");
				rawEncodedRadio.addSelectionListener
				(
					new SelectionAdapter()
					{
						@Override
                        public void widgetSelected(SelectionEvent event)
						{ 
							processButtonSelection(event);
						}
					}
				);
				currentLeft += 60;
			}
		}

		updateEncodingOptions();


		this.layout();
	}
	
	public byte[] getBody()
	{
		return activeControl.getBytes();
	}

	public void clearData()
	{
		body = new byte[0];
		try
        {
	        updateBodyControl();
        }
        catch (UpdateCanceledException e)
        {
	        Log.error("Not sure how this was reached", e);
        }
	}
	
}
