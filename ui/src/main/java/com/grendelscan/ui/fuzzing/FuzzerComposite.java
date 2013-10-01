package com.grendelscan.ui.fuzzing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.MessageBox;

import com.grendelscan.fuzzing.FuzzVector;
import com.grendelscan.fuzzing.Fuzzer;
import com.grendelscan.scan.Scan;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GText;
import com.grendelscan.ui.http.transactionTable.AbstractTransactionTable;
import com.grendelscan.ui.http.transactionTable.FuzzerResultsTable;
import com.grendelscan.ui.proxy.interception.InterceptFilter;
import com.grendelscan.ui.settings.GrendelSettingsControl;

public class FuzzerComposite extends com.grendelscan.ui.customControls.basic.GComposite implements GrendelSettingsControl
{

    final protected static String PAUSE = "Pause";
    final protected static String RESUME = "Resume";
    final protected static String START = "Start";
    protected final List<InterceptFilter> fuzzFilters;

    protected boolean usePlatformErrors = true;
    protected boolean exiting = false;
    protected FuzzVector fuzzVector;

    private GButton fuzzTemplateButton;
    private GButton fuzzVectorButton;
    private GButton fuzzCriteriaButton;
    private GLabel maxFuzzRequestsLabel;
    private GText maxFuzzRequestsTextbox;
    GButton startFuzzButton;
    private GButton stopFuzzButton;
    private GButton clearTableButton;
    private FuzzerResultsTable fuzzResultsTable;
    Fuzzer fuzzer;

    public FuzzerComposite(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style)
    {
        super(parent, style);
        fuzzFilters = new ArrayList<InterceptFilter>();
        initGUI();
    }

    public void fuzzDone()
    {
        getDisplay().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                startFuzzButton.setText(START);
                stopFuzzButton.setEnabled(false);
            }
        });
    }

    public final AbstractTransactionTable getFuzzResultsTable()
    {
        return fuzzResultsTable;
    }

    private void initGUI()
    {
        FormLayout thisLayout = new FormLayout();
        setLayout(thisLayout);

        {
            fuzzTemplateButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            fuzzTemplateButton.setText("Fuzz Template");
            FormData fuzzTemplateButtonLData = new FormData();
            fuzzTemplateButtonLData.width = 111;
            fuzzTemplateButtonLData.height = 27;
            fuzzTemplateButtonLData.top = new FormAttachment(0, 1000, 11);
            fuzzTemplateButtonLData.left = new FormAttachment(0, 1000, 12);
            fuzzTemplateButton.setLayoutData(fuzzTemplateButtonLData);
            fuzzTemplateButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    FuzzTemplateComposite.getFuzzTemplate(getShell());
                }
            });
        }
        {
            fuzzVectorButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            fuzzVectorButton.setText("Fuzz Vector");
            FormData fuzzVectorButtonLData = new FormData();
            fuzzVectorButtonLData.width = 111;
            fuzzVectorButtonLData.height = 27;
            fuzzVectorButtonLData.top = new FormAttachment(0, 1000, 11);
            fuzzVectorButtonLData.left = new FormAttachment(0, 1000, 149);
            fuzzVectorButton.setLayoutData(fuzzVectorButtonLData);
            fuzzVectorButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    fuzzVector = FuzzVectorChoiceDialog.getFuzzVector(getShell(), fuzzVector);
                }
            });
        }
        {
            fuzzCriteriaButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            fuzzCriteriaButton.setText("Fuzz Criteria");
            FormData fuzzCriteriaButtonLData = new FormData();
            fuzzCriteriaButtonLData.width = 110;
            fuzzCriteriaButtonLData.height = 27;
            fuzzCriteriaButtonLData.top = new FormAttachment(0, 1000, 11);
            fuzzCriteriaButtonLData.left = new FormAttachment(0, 1000, 288);
            fuzzCriteriaButton.setLayoutData(fuzzCriteriaButtonLData);
            fuzzCriteriaButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    usePlatformErrors = FuzzCriteriaDialog.showGUI(getShell(), fuzzFilters, usePlatformErrors);
                }
            });
        }
        {
            maxFuzzRequestsLabel = new GLabel(this, SWT.NONE);
            FormData maxFuzzRequestsLData = new FormData();
            maxFuzzRequestsLData.width = 158;
            maxFuzzRequestsLData.height = 17;
            maxFuzzRequestsLData.left = new FormAttachment(0, 1000, 12);
            maxFuzzRequestsLData.top = new FormAttachment(0, 1000, 54);
            maxFuzzRequestsLabel.setLayoutData(maxFuzzRequestsLData);
            maxFuzzRequestsLabel.setText("Max requests per vector:");
        }
        {
            maxFuzzRequestsTextbox = new GText(this, SWT.BORDER);
            FormData text1LData = new FormData();
            text1LData.width = 54;
            text1LData.height = 19;
            text1LData.left = new FormAttachment(0, 1000, 176);
            text1LData.top = new FormAttachment(0, 1000, 50);
            maxFuzzRequestsTextbox.setLayoutData(text1LData);
            maxFuzzRequestsTextbox.setText("0");
            maxFuzzRequestsTextbox.addVerifyListener(GuiUtils.integersOnlyVerifyer);
        }
        {
            startFuzzButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData startFuzzButtonLData = new FormData();
            startFuzzButtonLData.width = 67;
            startFuzzButtonLData.height = 27;
            startFuzzButtonLData.left = new FormAttachment(0, 1000, 260);
            startFuzzButtonLData.top = new FormAttachment(0, 1000, 52);
            startFuzzButton.setLayoutData(startFuzzButtonLData);
            startFuzzButton.setText(START);
            startFuzzButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    if (startFuzzButton.getText().equals(START))
                    {
                        startFuzzer();
                    }
                    else if (startFuzzButton.getText().equals(PAUSE))
                    {
                        fuzzer.setPaused(true);
                        startFuzzButton.setText(RESUME);
                    }
                    else if (startFuzzButton.getText().equals(RESUME))
                    {
                        fuzzer.setPaused(false);
                        startFuzzButton.setText(PAUSE);
                    }
                }
            });
        }
        {
            stopFuzzButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData stopFuzzButtonLData = new FormData();
            stopFuzzButtonLData.width = 55;
            stopFuzzButtonLData.height = 27;
            stopFuzzButtonLData.left = new FormAttachment(0, 1000, 333);
            stopFuzzButtonLData.top = new FormAttachment(0, 1000, 52);
            stopFuzzButton.setLayoutData(stopFuzzButtonLData);
            stopFuzzButton.setEnabled(false);
            stopFuzzButton.setText("Stop");
            stopFuzzButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    if (fuzzer != null)
                    {
                        fuzzer.stop();
                    }
                }
            });
        }
        {
            clearTableButton = new GButton(this, SWT.PUSH | SWT.CENTER);
            FormData clearTableButtonLData = new FormData();
            clearTableButtonLData.width = 85;
            clearTableButtonLData.height = 27;
            clearTableButtonLData.left = new FormAttachment(0, 1000, 394);
            clearTableButtonLData.top = new FormAttachment(0, 1000, 52);
            clearTableButton.setLayoutData(clearTableButtonLData);
            clearTableButton.setText("Clear GTable");
            clearTableButton.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(final SelectionEvent evt)
                {
                    fuzzer.getFuzzResults().clear();
                }
            });
        }

        this.layout();
    }

    protected void startFuzzer()
    {
        String message = "";
        if (fuzzVector == null)
        {
            message += "Please define a fuzz vector.\n";
        }
        if (FuzzTemplateComposite.fuzzTemplateText == null || FuzzTemplateComposite.fuzzTemplateText.equals(""))
        {
            message += "Please define a fuzz template.";
        }
        if (message.length() > 0)
        {
            MessageBox m = new MessageBox(getShell(), SWT.OK);
            m.setText("Error");
            m.setMessage(message);
            m.open();
        }
        else
        {
            fuzzer = new Fuzzer(fuzzVector, Integer.valueOf(maxFuzzRequestsTextbox.getText()), FuzzTemplateComposite.fuzzTemplateText, fuzzFilters, usePlatformErrors, this);
            if (fuzzResultsTable != null)
            {
                fuzzResultsTable.dispose();
            }
            {
                fuzzResultsTable = new FuzzerResultsTable(this, SWT.NONE, fuzzer.getFuzzResults());
                FormData fuzzResultsTableLData = new FormData();
                fuzzResultsTableLData.width = 805;
                fuzzResultsTableLData.height = 511;
                fuzzResultsTableLData.right = new FormAttachment(1000, 1000, -5);
                fuzzResultsTableLData.bottom = new FormAttachment(1000, 1000, -5);
                fuzzResultsTableLData.left = new FormAttachment(0, 1000, 5);
                fuzzResultsTableLData.top = new FormAttachment(0, 1000, 92);
                fuzzResultsTable.setLayoutData(fuzzResultsTableLData);
            }

            Scan.getInstance().getFuzzers().add(fuzzer);
            fuzzer.start();
            startFuzzButton.setText(PAUSE);
            stopFuzzButton.setEnabled(true);
        }
    }

    @Override
    public void updateFromSettings()
    {

    }

    @Override
    public String updateToSettings()
    {
        return "";
    }
}
