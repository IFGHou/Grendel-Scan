package com.grendelscan.GUI;

import java.io.File;
import java.security.GeneralSecurityException;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import com.grendelscan.GUI.customControls.basic.GShell;
import com.grendelscan.GUI.customControls.basic.GTabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.grendelscan.GrendelScan;
import com.grendelscan.GUI.UpdateService.UpdateService;
import com.grendelscan.GUI.actionComposites.ManualRequestComposite;
import com.grendelscan.GUI.fuzzing.FuzzerComposite;
import com.grendelscan.GUI.http.transactionTable.AbstractTransactionTable;
import com.grendelscan.GUI.http.transactionTable.AllTransactionTable;
import com.grendelscan.GUI.http.transactionTable.TransactionTableComposite;
import com.grendelscan.GUI.proxy.ProxyTabs;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.GUI.settings.NetworkSettingsComposite;
import com.grendelscan.GUI.settings.scanSettings.ScannerTabs;
import com.grendelscan.GUI.statusComposites.LogComposite;
import com.grendelscan.logging.Log;
import com.grendelscan.proxy.ssl.ca.CertificateAuthority;
import com.grendelscan.requester.http.transactions.HttpTransactionFields;
import com.grendelscan.scan.Scan;



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
public class MainWindow extends com.grendelscan.GUI.customControls.basic.GComposite implements GrendelSettingsControl
{

	protected static GShell shell;
	
	protected static MainWindow instance;
	protected boolean exiting;

	private MenuBar	menuBar;

	private FuzzerComposite			fuzzerComposite;
	private TabItem					fuzzerTab;
	private LogComposite			logComposite;
	private TabItem					logTab;
	private ManualRequestComposite	manualRequestComposite;
	private TabItem					manualRequestTab;
	private ProxyTabs				proxyComposite;
	private TabItem					proxyTab;
	private ScannerTabs				scannerTabs;
	private TabItem					scanSettingsTab;
	private GTabFolder				mainTabs;
	private AbstractTransactionTable		transactionsTableComposite;
	private TabItem					transactionsTab;
	private TabItem					networkTab;
	private UpdateService			updateService;
	private NetworkSettingsComposite networkComposite;
	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		GuiUtils.registerResourceUser(this);
	}
	
	public static void setWindowTitle(String text)
	{
		shell.setText(GrendelScan.versionText + " - " + text);
	}

	
	public static void startBareGUI()
	{
		final Display display = Display.getDefault();
		
		shell = new GShell(display);
		instance = new MainWindow(shell, SWT.NULL);
		shell.setLayout(new FillLayout());
//		display.asyncExec(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				Thread t = Thread.currentThread();
//				t.setName("GUI thread");
//				t.setPriority(Thread.MAX_PRIORITY);
//
//			}
//		});
	}
	
	public void showFullGUI()
	{
		initFullGUI();
		updateFromSettings();
		shell.open();
		final Display display = Display.getDefault();
		while (!shell.isDisposed())
		{
			try
			{
				if (!display.readAndDispatch())
					display.sleep();
			}
			catch (Exception e)
			{
				Log.error("Unhandled GUI exception: " + e.toString(), e);
			}
		} 
	}
	
	public String getOutputDir()
	{
		String outputDir = null;
		while(outputDir == null || outputDir.isEmpty())
		{
			DirectoryDialog dd = new DirectoryDialog(getShell());
			dd.setText("Output directory name");
			dd.setMessage("Output directory name");
			//            dd.setFilterPath(new File(".").getCanonicalPath());
			dd.setFilterPath(null);
			outputDir = dd.open();

			if (outputDir == null || outputDir.isEmpty())
			{
				int result = displayPrompt("Output Directory Required", 
						"An output directory must be specified before continuing. Click \"Cancel\" to exit.", 
						SWT.OK | SWT.CANCEL, true);
				if (result == SWT.CANCEL)
				{
					System.exit(1);
				}
			}
		}
		return outputDir;
	}

	private MainWindow(GComposite parent, int style) 
	{
		super(parent, style);
		exiting = false;
	}


	public void initFullGUI() 
	{
			updateService = new UpdateService();
			FillLayout thisLayout = new FillLayout(SWT.HORIZONTAL|SWT.VERTICAL);
			this.setLayout(thisLayout);

			this.setBackground(GuiUtils.getColor(192, 192, 192));
			Image icon = new Image(getDisplay(), GrendelScan.defaultConfigDirectory + File.separator + "icon.JPG");
			this.getShell().setImage(icon);
			menuBar = new MenuBar(shell);
			shell.addListener(SWT.Close, getCloseListener());
			
			mainTabs = new GTabFolder(this, SWT.NONE);
			{
				scanSettingsTab = new TabItem(mainTabs, SWT.NONE);
				scanSettingsTab.setText("Scanner");
				scannerTabs = new ScannerTabs(mainTabs, SWT.NONE);
				scanSettingsTab.setControl(scannerTabs);
			}

			{
				proxyTab = new TabItem(mainTabs, SWT.NONE);
				proxyTab.setText("Proxy");
				proxyComposite = new ProxyTabs(mainTabs, SWT.NONE);
				proxyTab.setControl(proxyComposite);
			}

			{
				fuzzerTab = new TabItem(mainTabs, SWT.NONE);
				fuzzerTab.setText("Fuzzer");
				fuzzerComposite = new FuzzerComposite(mainTabs, SWT.NONE);
				fuzzerTab.setControl(fuzzerComposite);
			}

			{
				manualRequestTab = new TabItem(mainTabs, SWT.NONE);
				manualRequestTab.setText("Manual Request");
				manualRequestComposite = new ManualRequestComposite(mainTabs, SWT.NONE);
				manualRequestTab.setControl(manualRequestComposite);
			}

			{
				transactionsTab = new TabItem(mainTabs, SWT.NONE);
				transactionsTab.setText("Transactions");
				transactionsTableComposite = new AllTransactionTable(mainTabs, SWT.NONE);
				TransactionTableComposite transactionComposite = new TransactionTableComposite(mainTabs, SWT.NONE, transactionsTableComposite);
				transactionsTab.setControl(transactionComposite);
			}
			
			{
				networkTab = new TabItem(mainTabs, SWT.NONE);
				networkTab.setText("Network");
				networkComposite = new NetworkSettingsComposite(mainTabs, SWT.NONE);
				networkTab.setControl(networkComposite);
			}

			{
				logTab = new TabItem(mainTabs, SWT.NONE);
				logTab.setText("Log");
				logComposite = new LogComposite(mainTabs, SWT.NONE);
				logTab.setControl(logComposite);
			}

			this.layout();
	}

	public Listener getCloseListener()
	{
		Listener l = new Listener () 
		{
			
			@Override
			public void handleEvent (Event event) 
			{
				event.doit = false;
				
				if (promptExit())
				{	
					exiting = true;
					Scan.getInstance().shutdown("Exit requested");
//					synchronized(this)
					{
						while (!Scan.getInstance().isShutdownComplete())
						{
							try
                            {
								Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                            	Log.debug("Scan shutdown interupted: " + e.toString(), e);
                            	break;
                            }
						}
					}
					event.doit = true;
				}
			}
		};
		return l;
	}
	
	

	
	protected boolean promptExit()
	{
		return SWT.YES == displayPrompt("Exit?", "Are you sure you want to exit?", 
				SWT.APPLICATION_MODAL | SWT.YES | SWT.NO, true);
	}

	
	
	public void displayMessage(final String title, final String message, boolean wait)
	{
		displayPrompt(title, message, SWT.OK, wait);
	}

	private class RunMessageBox implements Runnable
	{
		int params;
		String message;
		String title;
		int internalResult;

		public RunMessageBox(String title, String message, int params)
		{
			this.params = params;
			this.message = message;
			this.title = title;
		}

		@Override
		public void run()
		{
			MessageBox m = new MessageBox(instance.getShell(), params);
			m.setMessage(message);
			m.setText(title);
			internalResult = m.open();
		}
	}

	public int displayPrompt(String title, String message, int params, boolean wait)
	{
		int result = 0;
		if (!instance.getDisplay().isDisposed())
		{
			RunMessageBox rmb = new RunMessageBox(title, message, params);
			if (wait)
			{
				instance.getDisplay().syncExec(rmb);
			}
			else
			{
				instance.getDisplay().asyncExec(rmb);
			}
			result = rmb.internalResult;
		}
		return result;
	}

	

	public static final MainWindow getInstance()
	{
		return instance;
	}

	@Override
	public void updateFromSettings()
	{
		scannerTabs.updateFromSettings();
		proxyComposite.updateFromSettings();
	}

	public void importSettings()
	{
		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setFilterPath("." + File.separator +GrendelScan.defaultConfigDirectory + File.separator + Scan.getScanSettings().getScanConfigDir());
		fd.setFilterExtensions(new String[] {"*.scan"});
		fd.setText("Import settings file name");
		String filename = fd.open();
		if (filename != null)
		{
			try
            {
				Scan.getScanSettings().loadScanSettings(filename);
				updateFromSettings();
			}
            catch (ConfigurationException e1)
            {
            	displayMessage("Error", "Problem loading scan settings: \n" + e1.toString(), true);
            }
		}
	}


	public void saveSettings()
	{
		FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
		fd.setFilterPath("." + File.separator +GrendelScan.defaultConfigDirectory + 
				File.separator + Scan.getScanSettings().getScanConfigDir());
		fd.setFilterExtensions(new String[] {"*.scan"});
		fd.setText("Save settings file name");
		String filename = fd.open();
		if (filename != null)
		{
			try
            {
				Scan.getScanSettings().saveScanSettings(filename);
			}
            catch (ConfigurationException e1)
            {
            	displayMessage("Error", "Problem saving scan settings: \n" + e1.toString(), true);
            }
		}
	}


	
	public void handleExit()
	{
		if (promptExit())
		{
			getParent().dispose();
			System.exit(0);
		}
	}
	
	public void updateQueueText()
	{
		if (!isDisposed())
		{
			getDisplay().timerExec(250, new Runnable()
			{
				@Override
				public void run()
				{
					transactionsTableComposite.setTransactionCount(HttpTransactionFields.getTotalExecutions());
					logComposite.updateQueueSizes();
					updateQueueText();
				}
			});
		}
	}

	public void regenerateCA()
	{
		try
        {
            CertificateAuthority.regenerateCA();
        }
        catch (GeneralSecurityException e1)
        {
        	displayMessage("Error", "There was a problem regenerating the CA certificate: " + e1.getMessage(), true);
        }
	}
	

	public void showHelp(String topic)
	{
		
	}

	
	public void displayTransactionInManualRequest(int transactionID) 
	{
		getManualRequestComposite().displayTransactionInManualRequest(transactionID);
	}
	
	

	public void setSelection(TabItem item)
	{
		mainTabs.setSelection(item);
	}

	public final FuzzerComposite getFuzzerComposite()
	{
		return fuzzerComposite;
	}

	public final LogComposite getLogComposite()
	{
		return logComposite;
	}

	public final ManualRequestComposite getManualRequestComposite()
	{
		return manualRequestComposite;
	}

	public final ProxyTabs getProxyComposite()
	{
		return proxyComposite;
	}

	public final ScannerTabs getScannerTabs()
	{
		return scannerTabs;
	}

	public final AbstractTransactionTable getTransactionsComposite()
	{
		return transactionsTableComposite;
	}

	@Override
	public String updateToSettings()
	{
		return "";
	}

	public final TabItem getFuzzerTab()
	{
		return fuzzerTab;
	}

	public final TabItem getLogTab()
	{
		return logTab;
	}

	public final TabItem getProxyTab()
	{
		return proxyTab;
	}

	public final GTabFolder getMainTabs()
	{
		return mainTabs;
	}

	public final TabItem getTransactionsTab()
	{
		return transactionsTab;
	}

	public final TabItem getManualRequestTab()
	{
		return manualRequestTab;
	}


	public final UpdateService getUpdateService()
	{
		return updateService;
	}
	
	public void updateForwardProxyGUIStatus()
	{
		instance.getProxyComposite().getSettingsComposite().getProxyBindingsComposite().externalUpdateForwardProxyGUIStatus();
	}
}
