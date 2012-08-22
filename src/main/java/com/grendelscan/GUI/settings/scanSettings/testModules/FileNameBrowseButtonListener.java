package com.grendelscan.GUI.settings.scanSettings.testModules;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import com.grendelscan.tests.testModuleUtils.settings.FileNameOption;

public class FileNameBrowseButtonListener extends SelectionAdapter
{
	protected FileNameOption internalOption;
	protected Text internalFilenameTextBox;
	
	@Override
	public void widgetSelected(SelectionEvent event)
	{
		String filename = null; 
		if (internalOption.isDirectory())
		{
			DirectoryDialog dd = new DirectoryDialog(event.display.getActiveShell());
			try
            {
                dd.setFilterPath(new File(".").getCanonicalPath());
            }
            catch (IOException e1)
            {
            }
			dd.setText(internalOption.getName());
			filename = dd.open();
		}
		else
		{
			FileDialog fd = new FileDialog(event.display.getActiveShell(), SWT.SAVE);
			fd.setText(internalOption.getName());
			filename = fd.open();
		}
		if (filename != null)
		{
			internalFilenameTextBox.setText(filename);
			internalOption.setValue(filename);
		}
	}

	public FileNameBrowseButtonListener(FileNameOption internalOption, Text internalFilenameTextBox)
    {
        this.internalOption = internalOption;
        this.internalFilenameTextBox = internalFilenameTextBox;
    }
}
