package com.grendelscan.ui.http.interception;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.GrendelScan;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.http.HttpFormatException;
import com.grendelscan.commons.http.apache_overrides.serializable.SerializableStatusLine;
import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.commons.http.wrappers.HttpRequestWrapper;
import com.grendelscan.commons.http.wrappers.HttpResponseWrapper;
import com.grendelscan.scan.Scan;
import com.grendelscan.scan.TransactionSource;
import com.grendelscan.ui.MainWindow;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GShell;
import com.grendelscan.ui.http.transactionDisplay.TransactionComposite;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class InterceptionComposite extends com.grendelscan.ui.customControls.basic.GComposite
{
    private static class InterceptJob implements Runnable
    {
        StandardHttpTransaction transaction;
        boolean requestOnly;
        boolean changed;

        public InterceptJob(final StandardHttpTransaction transaction, final boolean requestOnly)
        {
            super();
            this.requestOnly = requestOnly;
            this.transaction = transaction;
        }

        @Override
        public void run()
        {
            Display display = Display.getDefault();
            GShell shell = new GShell(display);
            Image icon = new Image(display, GrendelScan.defaultConfigDirectory + File.separator + "icon.JPG");
            shell.setImage(icon);
            if (requestOnly)
            {
                shell.setText(GrendelScan.versionText + " - Request intercept");
            }
            else
            {
                shell.setText(GrendelScan.versionText + " - Response intercept");
            }

            InterceptionComposite inst = new InterceptionComposite(shell, SWT.NULL, transaction, requestOnly);
            Point size = inst.getSize();
            shell.setLayout(new FillLayout());
            shell.layout();
            if (size.x == 0 && size.y == 0)
            {
                inst.pack();
                shell.pack();
            }
            else
            {
                Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
                shell.setSize(shellBounds.width, shellBounds.height);
            }
            shell.open();
            while (!shell.isDisposed())
            {
                if (!display.readAndDispatch())
                {
                    display.sleep();
                }
            }
            transaction = inst.transaction;
            changed = inst.isChanged();
        }
    }

    public static StandardHttpTransaction showRequestIntercept(final StandardHttpTransaction transaction)
    {
        Display display = Display.getDefault();
        InterceptJob interceptJob = new InterceptJob(transaction, true);
        display.syncExec(interceptJob);
        return interceptJob.transaction;
    }

    private GButton acceptButton;
    private GButton abortButton;
    private GButton release;

    StandardHttpTransaction transaction;
    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptionComposite.class);

    public static boolean showResponseIntercept(final StandardHttpTransaction transaction)
    {
        Display display = Display.getDefault();
        InterceptJob interceptJob = new InterceptJob(transaction, false);
        display.syncExec(interceptJob);
        return interceptJob.changed;
    }

    private final boolean requestOnly;
    private TransactionComposite transactionComposite;

    GButton interceptResponsesButton;

    GButton interceptRequestsButton;

    private boolean changed = false;

    InterceptionComposite(final GComposite parent, final int style, final StandardHttpTransaction transaction, final boolean requestOnly)
    {
        super(parent, style);
        this.transaction = transaction;
        this.requestOnly = requestOnly;
        initGUI();
    }

    void abortTransaction()
    {
        changed = true;

        HttpResponseWrapper responseWrapper = new HttpResponseWrapper(0);
        responseWrapper.setStatusLine(new SerializableStatusLine(HttpRequestWrapper.DEFAULT_PROTOCL_VERSION, 500, "Transaction Aborted"));
        responseWrapper.getHeaders().addHeader("Content-Type", "text/html");
        responseWrapper.setBody(("<html><title>Transaction Aborted</title><body>This transaction was aborted by the user.<br>\n<br>\n<br>\n" + transaction.toString().replaceAll("\n", "<br>\n") + "</body></html>").getBytes(StringUtils.getDefaultCharset()));
        transaction.setResponseWrapper(responseWrapper);
        getParent().dispose();
    }

    void acceptButtonWidgetSelected(@SuppressWarnings("unused") final SelectionEvent evt) throws URISyntaxException
    {
        try
        {
            if (requestOnly)
            {
                transaction = transactionComposite.makeHttpRequest(TransactionSource.PROXY);
            }
            else
            {
                transaction.setResponseWrapper(transactionComposite.makeHttpResponseWrapper());
            }
            changed = true;
            getParent().dispose();
        }
        catch (HttpFormatException e)
        {
            MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.WRAP);
            m.setText("Invalid message format");
            m.setMessage("The format of the HTTP message is invalid. Future versions of Grendel-Scan will support " + "arbitrary request formats. For now, please use a format that resembles RFC 2616.\n" + "\n" + "The error message was: " + e.toString());
            m.open();
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
    }

    private void initGUI()
    {
        setLayout(new FormLayout());
        {
            interceptRequestsButton = new GButton(this, SWT.CHECK | SWT.LEFT);
            FormData interceptRequestsButtonLData = new FormData();
            interceptRequestsButtonLData.width = 138;
            interceptRequestsButtonLData.height = 19;
            interceptRequestsButtonLData.left = new FormAttachment(0, 1000, 5);
            interceptRequestsButtonLData.top = new FormAttachment(0, 1000, 5);
            interceptRequestsButton.setLayoutData(interceptRequestsButtonLData);
            interceptRequestsButton.setText("Intercept requests");
            interceptRequestsButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                {
                    MainWindow.getInstance().getProxyComposite().getInterceptionComposite().changeEnableInterceptRequestCheck(interceptRequestsButton.getSelection());
                }
            });
            interceptRequestsButton.setSelection(Scan.getScanSettings().isInterceptRequests());
        }
        {
            interceptResponsesButton = new GButton(this, SWT.CHECK | SWT.LEFT);
            FormData interceptResponsesButtonLData = new FormData();
            interceptResponsesButtonLData.width = 149;
            interceptResponsesButtonLData.height = 19;
            interceptResponsesButtonLData.left = new FormAttachment(0, 1000, 155);
            interceptResponsesButtonLData.top = new FormAttachment(0, 1000, 5);
            interceptResponsesButton.setLayoutData(interceptResponsesButtonLData);
            interceptResponsesButton.setText("Intercept responses");
            interceptResponsesButton.setSelection(Scan.getScanSettings().isInterceptResponses());
            interceptResponsesButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                {
                    MainWindow.getInstance().getProxyComposite().getInterceptionComposite().changeEnableInterceptResponseCheck(interceptResponsesButton.getSelection());
                }
            });
        }
        {
            if (requestOnly)
            {
                transactionComposite = new TransactionComposite(this, SWT.NONE, true, false, false);
                try
                {
                    transactionComposite.updateRequestData(transaction.getRequestWrapper().getBytes());
                }
                catch (URISyntaxException e)
                {
                    IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
                    LOGGER.error(e.toString(), e);
                    throw ise;
                }
            }
            else
            {
                transactionComposite = new TransactionComposite(this, SWT.NONE, false, true, true);
                try
                {
                    transactionComposite.updateTransactionData(transaction.getRequestWrapper().getBytes(), transaction.getResponseWrapper().getBytes());
                }
                catch (URISyntaxException e)
                {
                    IllegalStateException ise = new IllegalStateException("Really, really weird problem with uri parsing", e);
                    LOGGER.error(e.toString(), e);
                    throw ise;
                }
            }
            FormData transactionCompositeLData = new FormData();
            // transactionCompositeLData.width = 707;
            // transactionCompositeLData.height = 586;
            transactionCompositeLData.left = new FormAttachment(0, 1000, 0);
            transactionCompositeLData.right = new FormAttachment(1000, 1000, 0);
            transactionCompositeLData.top = new FormAttachment(35, 1000, 0);
            transactionCompositeLData.bottom = new FormAttachment(1000, 1000, -45);
            transactionComposite.setLayoutData(transactionCompositeLData);
        }
        {
            release = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData releaseLData = new FormData();
            releaseLData.width = 141;
            releaseLData.height = 27;
            releaseLData.left = new FormAttachment(0, 1000, 139);
            releaseLData.bottom = new FormAttachment(1000, 1000, -5);
            release.setLayoutData(releaseLData);
            release.setText("Release Unchanged");
            release.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                {
                    getParent().dispose();
                }
            });
        }
        {
            abortButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData abortButtonLData = new FormData();
            abortButtonLData.width = 125;
            abortButtonLData.height = 27;
            abortButtonLData.left = new FormAttachment(0, 1000, 292);
            abortButtonLData.bottom = new FormAttachment(1000, 1000, -5);
            abortButton.setLayoutData(abortButtonLData);
            abortButton.setText("Abort Transaction");
            abortButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                {
                    abortTransaction();
                }
            });
        }
        {
            acceptButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData acceptButtonLData = new FormData();
            acceptButtonLData.width = 115;
            acceptButtonLData.height = 27;
            acceptButtonLData.left = new FormAttachment(0, 1000, 12);
            acceptButtonLData.bottom = new FormAttachment(1000, 1000, -5);
            acceptButton.setLayoutData(acceptButtonLData);
            acceptButton.setText("Accept Changes");
            acceptButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    try
                    {
                        acceptButtonWidgetSelected(evt);
                    }
                    catch (URISyntaxException e)
                    {
                        MainWindow.getInstance().displayMessage("Error", "Illegal URI format: " + e.toString(), true);
                        evt.doit = false;
                    }
                }
            });
        }
        this.layout();
    }

    public boolean isChanged()
    {
        return changed;
    }
}
