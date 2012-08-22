package com.grendelscan.GUI.http.transactionDisplay;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.http.transactionDisplay.parsedEntityComposites.UrlEncodedViewComposite;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.TransactionSource;
import com.grendelscan.requester.http.HttpConstants;
import com.grendelscan.requester.http.transactions.StandardHttpTransaction;
import com.grendelscan.requester.http.wrappers.HttpRequestWrapper;
import com.grendelscan.utils.StringUtils;
import com.grendelscan.utils.URIStringUtils;



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
public class ParsedRequestComposite extends ScrolledComposite {
	private Label methodLabel;
	private Label urlLabel;
	private ParsedBodyComposite requestBodyComposite;
	private Group requestBodyGroup;
	private ParsedHttpHeaderComposite httpRequestHeaderList;
	private Group httpHeadersGroup;
	private UrlEncodedViewComposite urlQueryParameterList;
	private Group urlQueryParameterGroup;
	private Text httpVersionTextBox;
	private Text urlTextBox;
	private Text methodTextBox;
	private Label httpVersionLabel;
	private boolean editable;
	private Composite requestLineComposite;
	protected SashForm mainSash;
	protected static int[] weights;
	
	
	


	public ParsedRequestComposite(org.eclipse.swt.widgets.Composite parent, int style, boolean editable)
	{
		super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		this.editable = editable;
		
		initGUI();
	}

	
	public void updateData(HttpRequestWrapper request) throws URISyntaxException
	{
		if (request == null)
		{
			clearData();
			return;
		}
		httpRequestHeaderList.updateHeaderEncodedData(request.getHeaders().getReadOnlyHeaderArray());
		methodTextBox.setText(request.getMethod());
		urlTextBox.setText(URIStringUtils.getFileUri(request.getURI()));
		httpVersionTextBox.setText(request.getVersion().toString());
		urlQueryParameterList.updateData(URIStringUtils.getQuery(request.getURI()).getBytes(StringUtils.getDefaultCharset()));
		requestBodyComposite.updateData(request.getBody(), request.getHeaders().getMimeType());
		this.layout();
	}

	
	
	private void initGUI() 
	{
			addDisposeListener(
					new DisposeListener()
					{
						@Override
						public void widgetDisposed(@SuppressWarnings("unused") DisposeEvent arg0)
                        {
							weights = mainSash.getWeights();
                        }
					}
				);
			FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			this.setExpandHorizontal(true);
			this.setExpandVertical(true);
			mainSash = new SashForm(this, SWT.VERTICAL | SWT.V_SCROLL | SWT.NONE);
			mainSash.setBackground(SWTResourceManager.getColor(255, 255, 128));

			this.setContent(mainSash);
			{
				requestLineComposite = new Composite(mainSash, SWT.NONE);
				FormLayout requestLineCompositeLayout = new FormLayout();
				requestLineComposite.setLayout(requestLineCompositeLayout);
				{
					methodLabel = new Label(requestLineComposite, SWT.NONE);
					FormData methodLabelLData = new FormData();
					methodLabelLData.width = 58;
					methodLabelLData.height = 15;
					methodLabelLData.left =  new FormAttachment(0, 1000, 5);
					methodLabelLData.top =  new FormAttachment(0, 1000, 0);
					methodLabel.setLayoutData(methodLabelLData);
					methodLabel.setText("Method");
				}
				{
					FormData methodTextBoxLData = new FormData();
					methodTextBoxLData.width = 71;
					methodTextBoxLData.height = 19;
					methodTextBoxLData.left =  new FormAttachment(0, 1000, 5);
					methodTextBoxLData.top =  new FormAttachment(0, 1000, 15);
					methodTextBox = new Text(requestLineComposite, SWT.BORDER);
					methodTextBox.setEditable(editable);
					methodTextBox.setLayoutData(methodTextBoxLData);
				}
				{
					urlLabel = new Label(requestLineComposite, SWT.NONE);
					FormData urlLabelLData = new FormData();
					urlLabelLData.width = 50;
					urlLabelLData.height = 15;
					urlLabelLData.left =  new FormAttachment(0, 1000, 115);
					urlLabelLData.top =  new FormAttachment(0, 1000, 0);
					urlLabel.setLayoutData(urlLabelLData);
					urlLabel.setText("URL");
				}
	
				{
					FormData urlTextBoxLData = new FormData();
					urlTextBoxLData.width = 537;
					urlTextBoxLData.height = 19;
					urlTextBoxLData.left =  new FormAttachment(0, 1000, 115);
					urlTextBoxLData.top =  new FormAttachment(0, 1000, 15);
					urlTextBoxLData.right =  new FormAttachment(1000, 1000, -111);
					urlTextBox = new Text(requestLineComposite, SWT.BORDER);
					urlTextBox.setEditable(editable);
					urlTextBox.setLayoutData(urlTextBoxLData);
				}
				{
					httpVersionLabel = new Label(requestLineComposite, SWT.NONE);
					FormData httpVersionLabelLData = new FormData();
					httpVersionLabelLData.width = 93;
					httpVersionLabelLData.height = 15;
					httpVersionLabelLData.top =  new FormAttachment(0, 1000, 0);
					httpVersionLabelLData.right =  new FormAttachment(1000, 1000, 0);
					httpVersionLabel.setLayoutData(httpVersionLabelLData);
					httpVersionLabel.setText("HTTP Version");
				}
				{
					FormData httpVersionTextBoxLData = new FormData();
					httpVersionTextBoxLData.width = 75;
					httpVersionTextBoxLData.height = 19;
					httpVersionTextBoxLData.top =  new FormAttachment(0, 1000, 15);
					httpVersionTextBoxLData.right =  new FormAttachment(1000, 1000, -10);
					httpVersionTextBox = new Text(requestLineComposite, SWT.BORDER);
					httpVersionTextBox.setEditable(editable);
					httpVersionTextBox.setLayoutData(httpVersionTextBoxLData);
				}
				{
					urlQueryParameterGroup = new Group(requestLineComposite, SWT.NONE);
					FillLayout urlQueryParameterGroupLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
					urlQueryParameterGroup.setLayout(urlQueryParameterGroupLayout);
					FormData urlQueryParameterGroupLData = new FormData();
					urlQueryParameterGroupLData.width = 545;
					urlQueryParameterGroupLData.height = 100;
					urlQueryParameterGroupLData.left =  new FormAttachment(0, 1000, 115);
					urlQueryParameterGroupLData.top =  new FormAttachment(0, 1000, 40);
					urlQueryParameterGroupLData.right =  new FormAttachment(1000, 1000, -109);
					urlQueryParameterGroupLData.bottom =  new FormAttachment(1000, 1000, 0);
					urlQueryParameterGroup.setLayoutData(urlQueryParameterGroupLData);
					urlQueryParameterGroup.setText("URL query parameters");
					{
						urlQueryParameterList = new UrlEncodedViewComposite(urlQueryParameterGroup, SWT.NONE, 100, editable);
					}
				}
			}
			
			{
				httpHeadersGroup = new Group(mainSash, SWT.NONE);
				FillLayout httpHeadersGroupLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
				httpHeadersGroup.setLayout(httpHeadersGroupLayout);
				FormData httpHeadersGroupLData = new FormData();
				httpHeadersGroupLData.width = 742;
				httpHeadersGroupLData.height = 113;
				httpHeadersGroupLData.left =  new FormAttachment(0, 1000, 12);
				httpHeadersGroupLData.top =  new FormAttachment(213, 1000, 0);
				httpHeadersGroupLData.right =  new FormAttachment(1000, 1000, -8);
				httpHeadersGroupLData.bottom =  new FormAttachment(496, 1000, 0);
				httpHeadersGroup.setLayoutData(httpHeadersGroupLData);
				httpHeadersGroup.setText("HTTP headers");
				{
					httpRequestHeaderList = new ParsedHttpHeaderComposite(httpHeadersGroup, SWT.NONE, 200, editable, true);
				}
			}

			{
				requestBodyGroup = new Group(mainSash, SWT.NONE);
				FillLayout requestBodyGroupLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
				requestBodyGroup.setLayout(requestBodyGroupLayout);
				FormData requestBodyGroupLData = new FormData();
				requestBodyGroupLData.width = 751;
				requestBodyGroupLData.height = 295;
				requestBodyGroup.setLayoutData(requestBodyGroupLData);
				requestBodyGroup.setText("Request body");

				requestBodyComposite = new ParsedBodyComposite(requestBodyGroup, SWT.NONE, editable);
			}
			
			if (weights == null)
			{
				weights = new int[] {60,100,200};
			}
			mainSash.setWeights(weights);

			this.layout();

	}


	public String getUri()
	{
		String uri = urlTextBox.getText();

		if (urlQueryParameterList.getItemCount() > 0)
		{
			uri += "?" + URIStringUtils.urlEncode(urlQueryParameterList.getNameValuePairs());
		}
		return uri;
	}
	
	public String getMethod()
	{
		return methodTextBox.getText();
	}
	
	public String getHttpVersion()
	{
		return httpVersionTextBox.getText();
	}
	
	
	public Header[] getHeaders()
	{
		NameValuePair[] rawHeaders = httpRequestHeaderList.getNameValuePairs();
		Header[] headers = new Header[rawHeaders.length];
		int index = 0;
		for (NameValuePair rawHeader: rawHeaders)
		{
			headers[index++] = new BasicHeader(rawHeader.getName(), rawHeader.getValue());
		}
		return headers;
	}
	
	public byte[] getBody()
	{
		return requestBodyComposite.getBody();
	}
	
	public byte[] getBytes()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			out.write(getMethod().getBytes());
			out.write(' ');
			out.write(getUri().getBytes());
			out.write(' ');
			out.write(getHttpVersion().getBytes());
			out.write(HttpConstants.CRLF_BYTES);
			for (Header header: getHeaders())
			{
				out.write(header.getName().getBytes());
				out.write(':');
				out.write(' ');
				out.write(header.getValue().getBytes());
				out.write(HttpConstants.CRLF_BYTES);
			}
			out.write(HttpConstants.CRLF_BYTES);
			out.write(getBody());
		}
		catch (IOException e)
		{
			Log.error("Problem getting bytes for request: " + e.toString(), e);
		}
		return out.toByteArray();
	}
	
	@Override
	public String toString()
	{
		return new String(getBytes());
	}
	

	static private Pattern httpVersionPattern = Pattern.compile("^(\\w+)/(\\d+)\\.(\\d+)$");
	public StandardHttpTransaction makeHttpRequest(TransactionSource source) throws HttpFormatException
	{
		String uri = getUri();
		
//		String host;
//		String scheme;
//		int port;
//		try
//		{
//			host = URIStringUtils.getHost(uri);
//			if (host.equals(""))
//			{
//				host = httpRequestHeaderList.getFirstValue("Host", true);
//				if (host == null)
//				{
//					throw new HttpFormatException("Host header is required with a relative URL.");
//				}
//			}
//			scheme = URIStringUtils.getScheme(uri).toLowerCase();
//			if (scheme.equals(""))
//			{
//				scheme = "http";
//			}
//
//			port = URIStringUtils.getPort(uri);
//		}
//		catch (URISyntaxException e)
//		{
//			IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
//			Log.error(e.toString(), e);
//			throw ise;
//		}
//		if (port == 0)
//		{
//			if (scheme.equals("https"))
//			{
//				port = 443;
//			}
//			else
//			{
//				port = 80;
//			}
//		}
		
		
		String version = getHttpVersion().toUpperCase();
		Matcher m = httpVersionPattern.matcher(version);
		int major, minor;
		String protocol = "";
		if (m.find())
		{
			protocol = m.group(1);
			major = Integer.valueOf(m.group(2));
			minor = Integer.valueOf(m.group(3));
		}
		else
		{
			throw new HttpFormatException("Invalid protocol version format. It should look something like \"HTTP/1.0\".");
		}
		
		
		byte[] body = getBody();
		
		StandardHttpTransaction transaction = new StandardHttpTransaction(source, -1);
		transaction.getRequestWrapper().setURI(uri, true);
		transaction.getRequestWrapper().setBody(body);
		transaction.getRequestWrapper().setVersion(protocol, major, minor);
		transaction.getRequestWrapper().setMethod(getMethod());
//		transaction.getRequestWrapper().setNetworkHost(host);
//		transaction.getRequestWrapper().setNetworkPort(port);
		
		for(Header header: getHeaders())
		{
			transaction.getRequestWrapper().getHeaders().addHeader(header);
		}
		
		return transaction;
	}
	
	public void clearData()
	{
		httpVersionTextBox.setText("");
		urlTextBox.setText("");
		methodTextBox.setText("");
		httpRequestHeaderList.clearData();
		urlQueryParameterList.clearData();
		requestBodyComposite.clearData();
	}

	public void switchToParsed()
    {
	    requestBodyComposite.switchToParsed();
    }	
}
