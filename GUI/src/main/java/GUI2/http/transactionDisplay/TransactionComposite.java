package com.grendelscan.GUI2.http.transactionDisplay;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;

import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Display;
import com.grendelscan.GUI.customControls.basic.GShell;
import com.grendelscan.GUI.customControls.basic.GTabFolder;
import org.eclipse.swt.widgets.TabItem;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GrendelScan;
import com.grendelscan.GUI.MainWindow;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.wrappers.HttpRequestWrapper;
import com.grendelscan.requester.http.wrappers.HttpResponseWrapper;
import com.grendelscan.utils.HttpUtils;
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
public class TransactionComposite extends com.grendelscan.GUI.customControls.basic.GComposite {
//	protected SashForm mainSash;
	protected GTabFolder tabs;
	protected TabItem requestTab;
	protected TabItem responseTab;
	private GComposite httpRequestComposite;
	protected GButton requestParsedRadio;
	private GButton requestRawRadio;
	private FormData httpRequestControlLayoutData;
	private FormData httpResponseControlLayoutData;
	private GComposite httpResponseComposite;

	private ParsedRequestComposite parsedHttpRequest;
	private ParsedResponseComposite parsedHttpResponse;
	GText rawRequestTextBox;
	GText rawResponseTextBox;
//	private GButton nextButton;
//	private GButton previousButton ;

	private boolean requestEditable;
	private boolean responseEditable;
	protected boolean showResponse;
//	private boolean showNextButtons;
	protected GButton responseRawRadio;
	protected GButton responseParsedRadio;
	private byte[] rawRequest;
	private byte[] rawResponse;
	protected static boolean requestParsed = true;
	protected static boolean responseParsed = true;
	
	

	
	public static void displayTransaction(StandardHttpTransaction transaction, 
			boolean requestEditable, boolean responseEditable) 
	{
		Display display = Display.getDefault();
		GShell shell = new GShell(display);
		Image icon = new Image(display, GrendelScan.defaultConfigDirectory + File.separator + "icon.JPG");
		shell.setImage(icon);


		TransactionComposite inst = new TransactionComposite(shell, SWT.NULL, requestEditable, responseEditable, true);
		inst.displayTransactionData(transaction);
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


	public TransactionComposite(GComposite parent, int style, boolean requestEditable, boolean responseEditable, 
				boolean showResponse) 
	{
		super(parent, style);
		this.requestEditable = requestEditable;
		this.responseEditable = responseEditable;
//		this.showNextButtons = showNextButtons;
		this.showResponse = showResponse;
		initGUI();
	}

	public void updateTransactionData(byte[] newRawRequest, byte[] newRawResponse) throws URISyntaxException
	{
		updateRequestData(newRawRequest);
		updateResponseData(newRawResponse);
	}
	
	public void updateRequestData(byte[] newRawRequest) throws URISyntaxException 
	{
		this.rawRequest = newRawRequest;
        updateRequestControl(false);
	}
	
	public void updateResponseData(byte[] newRawResponse)
	{
		if (showResponse)
		{
			this.rawResponse = newRawResponse;
            updateResponseControl(false);
		}
	}
	
	
	
	private void initGUI() {
			this.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
			addDisposeListener(
				new DisposeListener()
				{
					@Override
					public void widgetDisposed(@SuppressWarnings("unused") DisposeEvent arg0)
                    {
						requestParsed = requestParsedRadio.getSelection();
						if (showResponse)
						{
							responseParsed = responseParsedRadio.getSelection();
						}
                    }
				}
			);
			{
				tabs = new GTabFolder(this, SWT.NONE);
				{
					{
						SelectionAdapter requestFormatChange = new SelectionAdapter()
						{
							@Override
		                    public void widgetSelected(SelectionEvent event)
							{
								if (((GButton)event.widget).getSelection())
									try
									{
										updateRequestControl(true);
									}
									catch (URISyntaxException e)
									{
										MainWindow.getInstance().displayMessage("Error", "Illegal uri format: " + e.toString(), true);
										event.doit = false;
									}
							}
						};

						httpRequestComposite = new GComposite(tabs, SWT.NONE);
						requestTab = new TabItem(tabs, SWT.NONE);
						requestTab.setText("Request");
						requestTab.setControl(httpRequestComposite);
						FormLayout httpRequestGroupLayout = new FormLayout();
						httpRequestComposite.setLayout(httpRequestGroupLayout);
						GridData httpRequestGroupLData = new GridData();
						httpRequestGroupLData.widthHint = 699;
						httpRequestGroupLData.heightHint = 283;
						httpRequestComposite.setLayoutData(httpRequestGroupLData);
						int buttonPadding = 0;
//						if (showNextButtons)
//						{
//							buttonPadding = 35;
//							{
//								previousButton = new GButton(httpRequestComposite, SWT.PUSH | SWT.CENTER);
//								FormData nextButtonLData = new FormData();
//								nextButtonLData.width = 70;
//								nextButtonLData.height = 25;
//								nextButtonLData.left =  new FormAttachment(0, 1000, 5);
//								nextButtonLData.top =  new FormAttachment(0, 1000, 5);
//								previousButton.setLayoutData(nextButtonLData);
//								previousButton.setText("Previous");
//								previousButton.addSelectionListener(new SelectionAdapter() {
//									@Override
//                                    public void widgetSelected(SelectionEvent evt) {
//										previousButtonSelected(evt);
//									}
//								});
//							}
//							{
//								nextButton = new GButton(httpRequestComposite, SWT.PUSH | SWT.CENTER);
//								FormData nextButtonLData = new FormData();
//								nextButtonLData.width = 70;
//								nextButtonLData.height = 25;
//								nextButtonLData.left =  new FormAttachment(0, 1000, 80);
//								nextButtonLData.top =  new FormAttachment(0, 1000, 5);
//								nextButton.setLayoutData(nextButtonLData);
//								nextButton.setText("Next");
//								nextButton.addSelectionListener(new SelectionAdapter() {
//									@Override
//                                    public void widgetSelected(SelectionEvent evt) {
//										nextButtonSelected(evt);
//									}
//								});
//							}
//						}
						{
							httpRequestControlLayoutData = new FormData();
							httpRequestControlLayoutData.left =  new FormAttachment(0, 1000, 5);
							httpRequestControlLayoutData.top =  new FormAttachment(0, 1000, 15 + buttonPadding);
							httpRequestControlLayoutData.right =  new FormAttachment(1000, 1000, -5);
							httpRequestControlLayoutData.bottom =  new FormAttachment(1000, 1000, -10);
						}
						{
							rawRequestTextBox = new GText(httpRequestComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
							rawRequestTextBox.setLayoutData(httpRequestControlLayoutData);
							rawRequestTextBox.setEditable(requestEditable);
							rawRequestTextBox.setVisible(!requestParsed);
							rawRequestTextBox.addModifyListener(new ModifyListener()
							{
								@Override
								public void modifyText(ModifyEvent arg0)
								{
									rawRequestTextBox.setData(null);
								}
							});
						}
						{
							parsedHttpRequest = new ParsedRequestComposite(httpRequestComposite, SWT.NONE, requestEditable);
							parsedHttpRequest.setLayoutData(httpRequestControlLayoutData);
							parsedHttpRequest.setVisible(requestParsed);
						}
						{
							requestParsedRadio = new GButton(httpRequestComposite, SWT.RADIO | SWT.LEFT);
							FormData requestParsedRadioLData = new FormData();
							requestParsedRadioLData.width = 69;
							requestParsedRadioLData.height = 15;
							requestParsedRadioLData.left =  new FormAttachment(0, 1000, 5);
							requestParsedRadioLData.top =  new FormAttachment(0, 1000, buttonPadding);
							requestParsedRadio.setLayoutData(requestParsedRadioLData);
							requestParsedRadio.setText("Parsed");
							requestParsedRadio.setSelection(requestParsed);
							requestParsedRadio.addSelectionListener(requestFormatChange);
						}
						{
							requestRawRadio = new GButton(httpRequestComposite, SWT.RADIO | SWT.LEFT);
							FormData requestRawRadioLData = new FormData();
							requestRawRadioLData.width = 49;
							requestRawRadioLData.height = 15;
							requestRawRadioLData.top =  new FormAttachment(0, 1000, buttonPadding);
							requestRawRadioLData.left =  new FormAttachment(0, 1000, 80);
							requestRawRadio.setLayoutData(requestRawRadioLData);
							requestRawRadio.setText("Raw");
							requestRawRadio.setSelection(!requestParsed);
							requestRawRadio.addSelectionListener(requestFormatChange);
						}
					}
					if (showResponse)
					{
						buildResponseTab();
					}

				}
				
			}
			this.layout();
	}
	
	private void buildResponseTab()
	{
		SelectionAdapter responseFormatChange = new SelectionAdapter()
		{
			@Override
            public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
			{
                updateResponseControl(true);
			}
		};

		httpResponseComposite = new GComposite(tabs, SWT.NONE);
		responseTab = new TabItem(tabs, SWT.NONE);
		responseTab.setText("Response");
		responseTab.setControl(httpResponseComposite);
		FormLayout httpResponseGroupLayout = new FormLayout();
		httpResponseComposite.setLayout(httpResponseGroupLayout);
		GridData httpRequestGroupLData = new GridData();
		httpRequestGroupLData.widthHint = 699;
		httpRequestGroupLData.heightHint = 283;
		httpResponseComposite.setLayoutData(httpRequestGroupLData);
		{
			httpResponseControlLayoutData = new FormData();
			httpResponseControlLayoutData.left =  new FormAttachment(0, 1000, 5);
			httpResponseControlLayoutData.top =  new FormAttachment(0, 1000, 25);
			httpResponseControlLayoutData.right =  new FormAttachment(1000, 1000, -5);
			httpResponseControlLayoutData.bottom =  new FormAttachment(1000, 1000, 0);
		}
		{
			parsedHttpResponse = new ParsedResponseComposite(httpResponseComposite, SWT.NONE, responseEditable);
			parsedHttpResponse.setLayoutData(httpResponseControlLayoutData);
			parsedHttpResponse.setVisible(responseParsed);
		}
		{
			rawResponseTextBox = new GText(httpResponseComposite, SWT.BORDER|SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
			rawResponseTextBox.setLayoutData(httpResponseControlLayoutData);
			rawResponseTextBox.setVisible(!responseParsed);
			rawResponseTextBox.setEditable(responseEditable);
			rawResponseTextBox.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent arg0)
				{
					rawResponseTextBox.setData(null);
				}
			});
		}
		{
			responseParsedRadio = new GButton(httpResponseComposite, SWT.RADIO | SWT.LEFT);
			FormData responseParsedRadioLData = new FormData();
			responseParsedRadioLData.width = 69;
			responseParsedRadioLData.height = 15;
			responseParsedRadioLData.left =  new FormAttachment(0, 1000, 5);
			responseParsedRadioLData.top =  new FormAttachment(0, 1000, 10);
			responseParsedRadio.setLayoutData(responseParsedRadioLData);
			responseParsedRadio.setText("Parsed");
			responseParsedRadio.setSelection(responseParsed);
			responseParsedRadio.addSelectionListener(responseFormatChange);
		}
		{
			responseRawRadio = new GButton(httpResponseComposite, SWT.RADIO | SWT.LEFT);
			FormData responseRawRadioLData = new FormData();
			responseRawRadioLData.width = 49;
			responseRawRadioLData.height = 15;
			responseRawRadioLData.top =  new FormAttachment(0, 1000, 10);
			responseRawRadioLData.left =  new FormAttachment(0, 1000, 80);
			responseRawRadio.setLayoutData(responseRawRadioLData);
			responseRawRadio.setText("Raw");
			responseRawRadio.setSelection(!responseParsed);
			responseRawRadio.addSelectionListener(responseFormatChange);
		}
	}
	
	
	public void switchToParsedRequest()
	{
		requestParsedRadio.setSelection(true);
		requestRawRadio.setSelection(false);
		rawResponseTextBox.setVisible(false);
		parsedHttpRequest.setVisible(true);
		parsedHttpRequest.switchToParsed();
	}
	
	public void clearData()
	{
		rawRequest = new byte[0];
		parsedHttpRequest.clearData();
		rawRequestTextBox.setText("");

		rawResponse = new byte[0];
		parsedHttpResponse.clearData();
		rawResponseTextBox.setText("");
		
	}
	
	public String getRequestText()
	{
		if (parsedHttpRequest.isVisible())
		{
			return parsedHttpRequest.toString();
		}
		return rawRequestTextBox.getText();
	}
	
	protected void updateRequestControl(boolean useActiveControl) throws URISyntaxException
	{
		byte[] oldRequest = rawRequest.clone();
		if (requestParsedRadio.getSelection() && (!useActiveControl || !parsedHttpRequest.isVisible()))
		{
			if (useActiveControl && requestEditable)
			{
				if (rawRequestTextBox.getData() == null) // If the field has been changed
				{
					rawRequest = rawRequestTextBox.getText().getBytes(StringUtils.getDefaultCharset());
				}
			}
			try
			{
				parsedHttpRequest.updateData(HttpUtils.parseRequest(rawRequest));
			}
			catch (IOException e)
			{
				MainWindow.getInstance().displayMessage("Error:", "Problem parsing request: " + e.toString(), true);
				return;
			}
			catch (HttpException e)
			{
				MainWindow.getInstance().displayMessage("Error:", "Problem parsing request: " + e.toString(), true);
				return;
			}
			rawRequestTextBox.setVisible(false);
			parsedHttpRequest.setVisible(true);
		}
		else if (requestRawRadio.getSelection() && (!useActiveControl || !rawRequestTextBox.isVisible()))
		{
			if (useActiveControl && requestEditable)
			{
				rawRequest = parsedHttpRequest.getBytes();
			}
			String displayedRequest = new String(rawRequest, StringUtils.getDefaultCharset());
			try
            {
	            displayedRequest = ParsedTransactionUtils.PrepareRawString(displayedRequest, null, requestEditable);
            }
            catch (UpdateCanceledException e)
            {
            	rawRequest = oldRequest;
            	requestParsedRadio.setSelection(true);
            	requestRawRadio.setSelection(false);
            	return;
            }
			rawRequestTextBox.setText(displayedRequest);
			rawRequestTextBox.setData(rawRequest);
			parsedHttpRequest.setVisible(false);
			rawRequestTextBox.setVisible(true);
		}
		httpRequestComposite.layout();
	}

	protected void updateResponseControl(boolean useActiveControl)
	{
		byte[] oldResponse = rawResponse;
		if (responseParsedRadio.getSelection() && (!useActiveControl || !parsedHttpResponse.isVisible()))
		{
			if (useActiveControl && responseEditable)
			{
				if (rawResponseTextBox.getData() == null)
				{
					rawResponse = rawResponseTextBox.getText().getBytes(StringUtils.getDefaultCharset());
				}
			}
			try
			{
				parsedHttpResponse.updateData(HttpUtils.parseResponse(rawResponse));
			}
			catch (IOException e)
			{
				MainWindow.getInstance().displayMessage("Error:", "Problem parsing response: " + e.toString(), true);
				return;
			}
			catch (HttpException e)
			{
				MainWindow.getInstance().displayMessage("Error:", "Problem parsing response: " + e.toString(), true);
				return;
			}
			rawResponseTextBox.setVisible(false);
			parsedHttpResponse.setVisible(true);
			
		}
		else if (responseRawRadio.getSelection() && (!useActiveControl || !rawResponseTextBox.isVisible()))
		{
			if (useActiveControl && responseEditable)
			{
				rawResponse = parsedHttpResponse.getBytes();
			}
			if (rawResponse == null)
				rawResponse = new byte[0];

			String displayedResponse = new String(rawResponse, StringUtils.getDefaultCharset());
			try
            {
	            displayedResponse = ParsedTransactionUtils.PrepareRawString(displayedResponse, null, responseEditable);
            }
            catch (UpdateCanceledException e)
            {
            	rawResponse = oldResponse;
            	return;
            }
			rawResponseTextBox.setText(displayedResponse);
			rawResponseTextBox.setData(rawResponse);
			parsedHttpResponse.setVisible(false);
			rawResponseTextBox.setVisible(true);
		}
		httpResponseComposite.layout();
	}

	public StandardHttpTransaction makeHttpRequest(TransactionSource source) throws HttpFormatException, IOException, HttpException
    {
		if (parsedHttpRequest.isVisible())
		{
			return parsedHttpRequest.makeHttpRequest(source);
		}
		HttpRequestWrapper wrapper = HttpUtils.parseRequest(rawRequestTextBox.getText().getBytes());
		StandardHttpTransaction transaction = new StandardHttpTransaction(source, -1);
		transaction.setRequestWrapper(wrapper);
		return transaction;
    }

//	public byte[] getRequestBody()
//	{
//		if (!parsedHttpRequest.isVisible())
//		{
//			try
//			{
//				parsedHttpRequest.updateData(HttpUtils.parseRequest(rawRequest));
//			}
//			catch (URISyntaxException e)
//			{
//				IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
//				Log.error(e.toString(), e);
//				throw ise;
//			}
//			catch (IOException e)
//			{
//				MainWindow.getInstance().displayMessage("Error:", "Problem parsing request: " + e.toString(), true);
//				return new byte[0];
//			}
//			catch (HttpException e)
//			{
//				MainWindow.getInstance().displayMessage("Error:", "Problem parsing request: " + e.toString(), true);
//				return new byte[0];
//			}
//		}
//		return parsedHttpRequest.getBody();
//	}
	
	public HttpResponseWrapper makeHttpResponseWrapper() throws HttpFormatException, IOException, HttpException
    {
		if (!parsedHttpResponse.isVisible())
		{
			parsedHttpResponse.updateData(HttpUtils.parseResponse(rawResponse));
		}
	    return parsedHttpResponse.makeHttpResponse();
    }

	
	
	public void displayTransactionData(StandardHttpTransaction transaction)
	{
		this.getShell().setText(GrendelScan.versionText + " - Transaction " + transaction.getId());
		byte[] request = transaction.getRequestWrapper().getBytes();
		byte[] response = new byte[0];
		if (showResponse && transaction.isResponsePresent())
		{
			response = transaction.getResponseWrapper().getBytes();
		}
        try
		{
			updateTransactionData(request, response);
		}
		catch (URISyntaxException e)
		{
			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
			Log.error(e.toString(), e);
			throw ise;
		}
	}


	public final void setShowResponse(boolean showResponse)
	{
		if (showResponse == this.showResponse)
			return;
		
		if (showResponse)
		{
			buildResponseTab();
		}
		else
		{
			httpResponseComposite.dispose();
			responseTab.dispose();
		}
		
		this.showResponse = showResponse;
	}

//
//	protected void nextButtonSelected(SelectionEvent evt) 
//	{
//		int nextID = transactionID + 1;
//		while (nextID <= AbstractHttpTransaction.getLastID())
//		{
//			AbstractHttpTransaction nextTransaction = Scan.getInstance().getTransactionRecord().getTransaction(nextID);
//			if (nextTransaction != null && nextTransaction.isSuccessfullExecution())
//			{
//				displayTransactionData(nextTransaction);
//				break;
//			}
//			nextID++;
//		}
//	}
//
//	protected void previousButtonSelected(SelectionEvent evt) 
//	{
//		int nextID = transactionID - 1;
//		while (nextID > 0)
//		{
//			AbstractHttpTransaction nextTransaction = Scan.getInstance().getTransactionRecord().getTransaction(nextID);
//			if (nextTransaction != null && nextTransaction.isSuccessfullExecution())
//			{
//				displayTransactionData(nextTransaction);
//				break;
//			}
//			nextID--;
//		}
//	}
	
}
