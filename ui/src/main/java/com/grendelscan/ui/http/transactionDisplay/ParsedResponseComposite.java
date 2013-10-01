package com.grendelscan.ui.http.transactionDisplay;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.commons.http.HttpConstants;
import com.grendelscan.commons.http.HttpFormatException;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableStatusLine;
import com.grendelscan.commons.http.wrappers.HttpResponseWrapper;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.Verifiers.EnforceIntegersOnly;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GText;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ParsedResponseComposite extends ScrolledComposite
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsedResponseComposite.class);

    private GLabel versionLabel;
    private GLabel codeLabel;
    private ParsedBodyComposite responseBodyComposite;
    private GGroup responseBodyGroup;
    private ParsedHttpHeaderComposite httpResponseHeaderList;
    private GGroup httpHeadersGroup;
    private GText httpMessageTextBox;
    private GText codeTextBox;
    private GText versionTextBox;
    private GLabel messageLabel;
    private final boolean editable;
    private GComposite responseHeadComposite;
    protected SashForm mainSash;
    private final boolean initialized = false;
    protected static int[] weights;

    static private Pattern httpVersionPattern = Pattern.compile("^(\\w+)/(\\d+)\\.(\\d+)$");

    public ParsedResponseComposite(final GComposite parent, final int style, final boolean editable)
    {
        super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
        this.editable = editable;

        initGUI();
    }

    public void clearData()
    {
        httpResponseHeaderList.clearData();
        codeTextBox.setText("");
        versionTextBox.setText("");
        httpMessageTextBox.setText("");
        responseBodyComposite.clearData();
    }

    public byte[] getBytes()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            out.write(getHttpVersion().getBytes());
            out.write(' ');
            out.write(getCode().getBytes());
            out.write(' ');
            out.write(getHttpMessage().getBytes());
            out.write(HttpConstants.CRLF_BYTES);
            for (Header header : getHeaders())
            {
                out.write(header.getName().getBytes());
                out.write(':');
                out.write(' ');
                out.write(header.getValue().getBytes());
                out.write(HttpConstants.CRLF_BYTES);
            }
            out.write(HttpConstants.CRLF_BYTES);
            out.write(responseBodyComposite.getBody());
        }
        catch (IOException e)
        {
            LOGGER.error("Problem getting bytes for response: " + e.toString(), e);
        }

        return out.toByteArray();
    }

    public String getCode()
    {
        return codeTextBox.getText();
    }

    public Header[] getHeaders()
    {
        NameValuePair[] rawHeaders = httpResponseHeaderList.getNameValuePairs();
        Header[] headers = new Header[rawHeaders.length];
        int index = 0;
        for (NameValuePair rawHeader : rawHeaders)
        {
            headers[index++] = new BasicHeader(rawHeader.getName(), rawHeader.getValue());
        }
        return headers;
    }

    public String getHttpMessage()
    {
        return httpMessageTextBox.getText();
    }

    public String getHttpVersion()
    {
        return versionTextBox.getText();
    }

    private void initGUI()
    {
        addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(@SuppressWarnings("unused") final DisposeEvent arg0)
            {
                weights = mainSash.getWeights();
            }
        });
        FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
        setLayout(thisLayout);
        this.setSize(800, 300);
        this.setMinSize(800, 300);
        setExpandHorizontal(true);
        setExpandVertical(true);

        mainSash = new SashForm(this, SWT.VERTICAL | SWT.V_SCROLL | SWT.NONE);
        FormLayout mainSashLayout = new FormLayout();
        mainSash.setLayout(mainSashLayout);
        mainSash.setBackground(GuiUtils.getColor(255, 255, 128));

        setContent(mainSash);
        {
            responseHeadComposite = new GComposite(mainSash, SWT.NONE);
            FormLayout responseHeadCompositeLayout = new FormLayout();
            FormData responseHeadCompositeLData = new FormData();
            responseHeadComposite.setLayoutData(responseHeadCompositeLData);
            responseHeadComposite.setLayout(responseHeadCompositeLayout);
            {
                versionLabel = new GLabel(responseHeadComposite, SWT.NONE);
                FormData versionLabelLData = new FormData();
                versionLabelLData.width = 58;
                versionLabelLData.height = 15;
                versionLabelLData.left = new FormAttachment(0, 1000, 5);
                versionLabelLData.top = new FormAttachment(0, 1000, 0);
                versionLabel.setLayoutData(versionLabelLData);
                versionLabel.setText("Version");
            }
            {
                FormData versionTextBoxLData = new FormData();
                versionTextBoxLData.width = 65;
                versionTextBoxLData.height = 19;
                versionTextBoxLData.left = new FormAttachment(0, 1000, 5);
                versionTextBoxLData.top = new FormAttachment(0, 1000, 15);
                versionTextBox = new GText(responseHeadComposite, SWT.BORDER);
                versionTextBox.setEditable(editable);
                versionTextBox.setLayoutData(versionTextBoxLData);
            }
            {
                codeLabel = new GLabel(responseHeadComposite, SWT.NONE);
                FormData codeLabelLData = new FormData();
                codeLabelLData.width = 35;
                codeLabelLData.height = 15;
                codeLabelLData.left = new FormAttachment(0, 1000, 85);
                codeLabelLData.top = new FormAttachment(0, 1000, 0);
                codeLabel.setLayoutData(codeLabelLData);
                codeLabel.setText("Code");
            }

            {
                FormData codeTextBoxLData = new FormData();
                codeTextBoxLData.width = 30;
                codeTextBoxLData.height = 19;
                codeTextBoxLData.left = new FormAttachment(0, 1000, 85);
                codeTextBoxLData.top = new FormAttachment(0, 1000, 15);
                codeTextBox = new GText(responseHeadComposite, SWT.BORDER);
                codeTextBox.setEditable(editable);
                codeTextBox.setLayoutData(codeTextBoxLData);
                codeTextBox.setTextLimit(3);
                codeTextBox.addVerifyListener(new EnforceIntegersOnly());
            }
            {
                messageLabel = new GLabel(responseHeadComposite, SWT.NONE);
                FormData messageLabelLData = new FormData();
                messageLabelLData.width = 97;
                messageLabelLData.height = 15;
                messageLabelLData.top = new FormAttachment(0, 1000, 0);
                messageLabelLData.left = new FormAttachment(0, 1000, 130);
                messageLabel.setLayoutData(messageLabelLData);
                messageLabel.setText("Message");
            }
            {
                FormData httpMessageTextBoxLData = new FormData();
                httpMessageTextBoxLData.width = 63;
                httpMessageTextBoxLData.height = 19;
                httpMessageTextBoxLData.top = new FormAttachment(0, 1000, 15);
                httpMessageTextBoxLData.right = new FormAttachment(1000, 1000, -5);
                httpMessageTextBoxLData.left = new FormAttachment(0, 1000, 130);
                httpMessageTextBox = new GText(responseHeadComposite, SWT.BORDER);
                httpMessageTextBox.setEditable(editable);
                httpMessageTextBox.setLayoutData(httpMessageTextBoxLData);
            }
        }

        {
            httpHeadersGroup = new GGroup(responseHeadComposite, SWT.NONE);
            FillLayout httpHeadersGroupLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
            httpHeadersGroup.setLayout(httpHeadersGroupLayout);
            FormData httpHeadersGroupLData = new FormData();
            httpHeadersGroupLData.width = 742;
            httpHeadersGroupLData.height = 113;
            httpHeadersGroupLData.left = new FormAttachment(0, 1000, 0);
            httpHeadersGroupLData.top = new FormAttachment(0, 1000, 40);
            httpHeadersGroupLData.right = new FormAttachment(1000, 1000, 0);
            httpHeadersGroupLData.bottom = new FormAttachment(1000, 1000, 0);
            httpHeadersGroup.setLayoutData(httpHeadersGroupLData);
            httpHeadersGroup.setText("HTTP headers");
            {
                httpResponseHeaderList = new ParsedHttpHeaderComposite(httpHeadersGroup, SWT.NONE, 200, editable, true);
            }
        }

        {
            responseBodyGroup = new GGroup(mainSash, SWT.NONE);
            FillLayout requestBodyGroupLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
            responseBodyGroup.setLayout(requestBodyGroupLayout);
            FormData requestBodyGroupLData = new FormData();
            responseBodyGroup.setLayoutData(requestBodyGroupLData);
            responseBodyGroup.setText("Response body");

            responseBodyComposite = new ParsedBodyComposite(responseBodyGroup, SWT.NONE, editable);
        }
        if (weights == null)
        {
            weights = new int[] { 110, 130 };
        }
        mainSash.setWeights(weights);

        this.layout();

    }

    public HttpResponseWrapper makeHttpResponse() throws HttpFormatException
    {
        Matcher m = httpVersionPattern.matcher(getHttpVersion());
        if (!m.find())
        {
            throw new HttpFormatException("Invalid protocol version format. It should look something like \"HTTP/1.0\".");
        }

        HttpResponseWrapper responseWrapper = new HttpResponseWrapper(0);
        responseWrapper.setStatusLine(new SerializableStatusLine(new ProtocolVersion(m.group(1), Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3))), Integer.valueOf(codeTextBox.getText()), httpMessageTextBox.getText()));
        responseWrapper.setBody(responseBodyComposite.getBody());
        responseWrapper.getHeaders().addHeaders(getHeaders());
        return responseWrapper;
    }

    // private void adjustSashHeight(int increment, int component)
    // {
    // int minComponentSize = 10;
    // int[] w = mainSash.getWeights();
    // float totalW = 0;
    // for (int i = 0; i < w.length; i++)
    // {
    // totalW += w[i];
    // }
    // Rectangle oldSashBounds = mainSash.getBounds();
    //
    // float weightPerPixel = (totalW / oldSashBounds.height);
    //
    // // don't try to shrink it to a negative size
    // int componentSize = Math.round(w[component] / weightPerPixel);
    // if (componentSize + increment < minComponentSize)
    // {
    // increment = minComponentSize - componentSize;
    // }
    //
    // w[component] += weightPerPixel * increment;
    //
    // mainSash.setWeights(w);
    // changeHeight(increment, mainSash);
    // // rect.height += increment;
    // // mainSash.setBounds(rect);
    // // this.setBounds(rect)
    // }

    // private void changeHeight(int increment, GComposite composite)
    // {
    // // // Weird bug? where groups don't resize normally
    // // if (composite instanceof GGroup)
    // // {
    // // ((FormData) composite.getLayoutData()).height += increment;
    // // }
    // // else
    // {
    // Rectangle bounds = composite.getBounds();
    // bounds.height += increment;
    // composite.setBounds(bounds);
    // }
    // composite.layout(true);
    // if (composite.getParent() != null)
    // {
    // changeHeight(increment, composite.getParent());
    // }
    // }

    // public void clearData()
    // {
    // if (responseBodyComposite != null)
    // responseBodyComposite.clearData();
    // httpResponseHeaderList.clearData();
    // httpMessageTextBox.setText("");
    // codeTextBox.setText("");
    // versionTextBox.setText("");
    // }

    @Override
    public String toString()
    {
        return new String(getBytes());
    }

    public void updateData(final HttpResponseWrapper response)
    {
        if (response == null)
        {
            clearData();
            return;
        }
        httpResponseHeaderList.updateHeaderEncodedData(response.getHeaders().getReadOnlyHeaderArray());
        codeTextBox.setText(response.getStatusLine().getStatusCode() + "");
        versionTextBox.setText(response.getStatusLine().getProtocolVersion().toString());
        httpMessageTextBox.setText(response.getStatusLine().getReasonPhrase());
        String mimeType = response.getHeaders().getMimeType();
        responseBodyComposite.updateData(response.getBody(), mimeType);
    }

}
