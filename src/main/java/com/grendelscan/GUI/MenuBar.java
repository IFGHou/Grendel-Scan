package com.grendelscan.GUI;

import org.apache.log4j.Level;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;

import com.grendelscan.GrendelScan;
import com.grendelscan.logging.Log;
import com.grendelscan.scan.Scan;

public class MenuBar 
{
	private Menu menuBar;
	MenuItem	selectedLoggingLevel;
	private MenuItem	pauseScanMenuItem;
	
	public MenuBar(Control parent)
	{
		menuBar = new Menu(parent.getShell(), SWT.BAR);
		menuBar.getShell().setMenuBar(menuBar);
		init();
	}
	
	@SuppressWarnings("unused")
	private void init()
	{
		{
			MenuItem fileMenuItem = new MenuItem (menuBar, SWT.CASCADE);
			fileMenuItem.setText ("&File");
			Menu fileMenu = new Menu (menuBar.getShell(), SWT.DROP_DOWN);
			fileMenuItem.setMenu(fileMenu);
			{
				MenuItem newScanMenuItem = new MenuItem (fileMenu, SWT.PUSH);
				newScanMenuItem.setText("&New Session");
				newScanMenuItem.addListener (SWT.Selection, new Listener () 
				{
					@Override
					public void handleEvent (Event e) 
					{
						int result = MainWindow.getInstance().displayPrompt("Confirm", "Are you sure you want to start a new scan?", SWT.YES | SWT.NO, true);
						if (result == SWT.YES)
						{
							Scan.getInstance().shutdown("New session will start soon");
							Scan.instantiate(true, MainWindow.getInstance().getOutputDir());
						}
					}
				});
			}

			{
				MenuItem loadPreviousScanMenuItem = new MenuItem (fileMenu, SWT.PUSH);
				loadPreviousScanMenuItem.setText("&Load Previous Session");
				loadPreviousScanMenuItem.addListener (SWT.Selection, new Listener () 
				{
					@Override
					public void handleEvent (Event e) 
					{
					}
				});
			}
			
			{
				MenuItem openScanSettingsMenuItem = new MenuItem (fileMenu, SWT.PUSH);
				openScanSettingsMenuItem.setText("&Import Settings");
				openScanSettingsMenuItem.addListener (SWT.Selection, new Listener () 
				{
					@Override
					public void handleEvent (Event e) 
					{
						MainWindow.getInstance().importSettings();
					}
				});
			}
			{
				MenuItem saveScanSettingsMenuItem = new MenuItem (fileMenu, SWT.PUSH);
				saveScanSettingsMenuItem.setText("&Save Scan Settings");
				saveScanSettingsMenuItem.addListener (SWT.Selection, new Listener () 
				{
					@Override
					public void handleEvent (Event e) 
					{
						MainWindow.getInstance().saveSettings();
					}
				});

			}

			{
				new MenuItem (fileMenu, SWT.SEPARATOR);
			}
			{
				MenuItem exitMenuItem = new MenuItem (fileMenu, SWT.PUSH);
				exitMenuItem.setText("E&xit");
				exitMenuItem.addListener (SWT.Selection, new Listener () {
					@Override
					public void handleEvent (Event e) 
					{
						MainWindow.getInstance().handleExit();
					}
				});
			}
		}
		{
			MenuItem toolsItem = new MenuItem (menuBar, SWT.CASCADE);
			toolsItem.setText ("&Tools");
			Menu toolsMenu = new Menu (menuBar.getShell(), SWT.DROP_DOWN);
			toolsItem.setMenu(toolsMenu);
			{
				MenuItem regenerateCAMenuItem = new MenuItem (toolsMenu, SWT.PUSH);
				regenerateCAMenuItem.setText("&Regenerate CA");
				regenerateCAMenuItem.addListener (SWT.Selection, new Listener () 
				{
					@Override
					public void handleEvent (Event e) 
					{
						MainWindow.getInstance().regenerateCA();
					}
				});
			}
		}
		{
			MenuItem scanItem = new MenuItem (menuBar, SWT.CASCADE);
			scanItem.setText ("&Scan");
			Menu scanMenu = new Menu (menuBar.getShell(), SWT.DROP_DOWN);
			scanItem.setMenu(scanMenu);
			{
				pauseScanMenuItem = new MenuItem (scanMenu, SWT.PUSH);
				updatePauseStatus();
				pauseScanMenuItem.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
						synchronized(Scan.getInstance())
						{
							Scan.getInstance().setPaused(!Scan.getInstance().isPaused());
						}
						updatePauseStatus();
					}
				});

			}
			{
				final MenuItem generateReportMenuItem = new MenuItem (scanMenu, SWT.PUSH);
				generateReportMenuItem.setText("&Generate Report");
				generateReportMenuItem.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
//						scanSettings.getReportGenerator().generateReport();
					}
				});

			}
			{
				final MenuItem initiateRecrawlMenuItem = new MenuItem (scanMenu, SWT.PUSH);
				initiateRecrawlMenuItem.setText("&Initiate Recrawl");
				initiateRecrawlMenuItem.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
					}
				});

			}

		}
		{
			MenuItem helpItem = new MenuItem (menuBar, SWT.CASCADE);
			helpItem.setText ("&Help");
			Menu helpMenu = new Menu (menuBar.getShell(), SWT.DROP_DOWN);
			helpItem.setMenu(helpMenu);
			{
				MenuItem item = new MenuItem (helpMenu, SWT.PUSH);
				item.setText("&Help Contents");
				item.addListener (SWT.Selection, new Listener () {
					@Override
					public void handleEvent (Event e) {
						MainWindow.getInstance().showHelp("");
					}
				});
			}
			{
				MenuItem statusTextItem = new MenuItem (helpMenu, SWT.PUSH);
				statusTextItem.setText("&Generate status text");
				statusTextItem.addListener (SWT.Selection, new Listener () {
					@Override
					public void handleEvent (Event e) {
						Log.warn(Scan.getInstance().generateStatus());
					}
				});
			}
			{
				MenuItem logLevelItem = new MenuItem (helpMenu, SWT.CASCADE);
				logLevelItem.setText("&Log Level");
				Menu logLevelMenu = new Menu (menuBar.getShell(), SWT.DROP_DOWN);
				logLevelItem.setMenu(logLevelMenu);
				{
					final MenuItem item = new MenuItem (logLevelMenu, SWT.CHECK);
					item.setText("&None");
					item.addListener (SWT.Selection, new Listener () {
						@Override
						public void handleEvent (Event e) {
							Log.setLevel(Level.OFF);
							selectedLoggingLevel.setSelection(false);
							selectedLoggingLevel = item;
							item.setSelection(true);
						}
					});
				}
				{
					final MenuItem item = new MenuItem (logLevelMenu, SWT.CHECK);
					item.setText("&Fatal");
					item.addListener (SWT.Selection, new Listener () {

						@Override
						public void handleEvent (Event e) {
							Log.setLevel(Level.FATAL);
							selectedLoggingLevel.setSelection(false);
							selectedLoggingLevel = item;
							item.setSelection(true);
						}
					});
				}
				{
					final MenuItem item = new MenuItem (logLevelMenu, SWT.CHECK);
					selectedLoggingLevel = item;
					item.setSelection(true);
					item.setText("&Warning");
					item.addListener (SWT.Selection, new Listener () {
						@Override
						public void handleEvent (Event e) {
							Log.setLevel(Level.WARN);
							selectedLoggingLevel.setSelection(false);
							selectedLoggingLevel = item;
							item.setSelection(true);
						}
					});
				}
				{
					final MenuItem item = new MenuItem (logLevelMenu, SWT.CHECK);
					item.setText("&Informational");
					item.addListener (SWT.Selection, new Listener () {
						@Override
						public void handleEvent (Event e) {
							Log.setLevel(Level.INFO);
							selectedLoggingLevel.setSelection(false);
							selectedLoggingLevel = item;
							item.setSelection(true);
						}
					});
				}
				{
					final MenuItem item = new MenuItem (logLevelMenu, SWT.CHECK);
					item.setText("&Debugging");
					item.addListener (SWT.Selection, new Listener () {
						@Override
						public void handleEvent (Event e) {
							Log.setLevel(Level.DEBUG);
							selectedLoggingLevel.setSelection(false);
							selectedLoggingLevel = item;
							item.setSelection(true);
						}
					});
				}
				{
					final MenuItem item = new MenuItem (logLevelMenu, SWT.CHECK);
					item.setText("&Trace");
					item.addListener (SWT.Selection, new Listener () {
						@Override
						public void handleEvent (Event e) {
							Log.setLevel(Level.TRACE);
							selectedLoggingLevel.setSelection(false);
							selectedLoggingLevel = item;
							item.setSelection(true);
						}
					});
				}
			}
			{
				MenuItem item = new MenuItem (helpMenu, SWT.PUSH);
				item.setText("&About");
				item.addListener (SWT.Selection, new Listener () {
					@Override
					public void handleEvent (Event e) {
						MessageBox box = new MessageBox(menuBar.getShell());
						box.setText("About " + GrendelScan.versionText);
						box.setMessage(GrendelScan.versionText + "\n" +
								"\n" +
								"Grendel-Scan was created by David Byrne.\n" + 
								"It is licensed under the GNU\n" +
								"Public License version 3 (GPLv3).\n" +
								"\n" +
								"Please visit the website at\n" +
								"www.grendel-scan.com for more information.");
						box.open();
					}
				});
			}
		}	
		updatePauseStatus();
	}

	void updatePauseStatus()
	{
		if (Scan.getInstance().isPaused())
		{
			pauseScanMenuItem.setText("&Resume Scan");
			MainWindow.setWindowTitle("PAUSED");
		}
		else
		{
			pauseScanMenuItem.setText("&Pause Scan");
			MainWindow.setWindowTitle("RUNNING");
		}
	}

}
