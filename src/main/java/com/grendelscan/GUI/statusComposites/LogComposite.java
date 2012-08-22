package com.grendelscan.GUI.statusComposites;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;
import com.grendelscan.scan.Scan;

public class LogComposite extends org.eclipse.swt.widgets.Composite
{


	private Label	categorizerQueueLabel;
	private Text	categorizerQueueSizeTextBox;
	private Label	requesterQueueLabel;
	private Text	requesterQueueSizeTextBox;
	Text	scanStatusTextArea;
	private Text	testerQueueSizeTextBox;
	private Label	testQueueLabel;

	public LogComposite(org.eclipse.swt.widgets.Composite parent, int style)
	{
		super(parent, style);
		initGUI();
	}

	private void initGUI()
	{
			FormLayout logLayout = new FormLayout();
			setLayout(logLayout);
			Group queueSizeGroup = new Group(this, SWT.NONE);
			queueSizeGroup.setLayout(null);
			FormData queueSizeGroupLData = new FormData();
			queueSizeGroupLData.width = 471;
			queueSizeGroupLData.height = 51;
			queueSizeGroupLData.left = new FormAttachment(0, 1000, 5);
			queueSizeGroupLData.bottom = new FormAttachment(1000, 1000, -5);
			queueSizeGroup.setLayoutData(queueSizeGroupLData);
			queueSizeGroup.setText("Queue Sizes");
			queueSizeGroup.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
					false, false));
			{
				testerQueueSizeTextBox = new Text(queueSizeGroup, SWT.READ_ONLY | SWT.BORDER);
				testerQueueSizeTextBox.setBounds(73, 31, 75, 25);
				testerQueueSizeTextBox.setBackground(SWTResourceManager.getColor(255, 255, 255));
				testerQueueSizeTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
			}
			{
				testQueueLabel = new Label(queueSizeGroup, SWT.NONE);
				testQueueLabel.setText("Tester:");
				testQueueLabel.setBounds(14, 31, 51, 25);
				testQueueLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
			}
			{
				categorizerQueueSizeTextBox = new Text(queueSizeGroup, SWT.READ_ONLY | SWT.BORDER);
				categorizerQueueSizeTextBox.setBounds(247, 31, 55, 25);
				categorizerQueueSizeTextBox.setBackground(SWTResourceManager.getColor(255, 255, 255));
				categorizerQueueSizeTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
			}
			{
				requesterQueueSizeTextBox = new Text(queueSizeGroup, SWT.READ_ONLY | SWT.BORDER);
				requesterQueueSizeTextBox.setBounds(398, 31, 55, 25);
				requesterQueueSizeTextBox.setBackground(SWTResourceManager.getColor(255, 255, 255));
				requesterQueueSizeTextBox.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
			}
			{
				requesterQueueLabel = new Label(queueSizeGroup, SWT.NONE);
				requesterQueueLabel.setText("Categorizer:");
				requesterQueueLabel.setBounds(160, 31, 73, 25);
				requesterQueueLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
			}
			{
				categorizerQueueLabel = new Label(queueSizeGroup, SWT.NONE);
				categorizerQueueLabel.setText("Requester:");
				categorizerQueueLabel.setBounds(314, 31, 81, 25);
				categorizerQueueLabel.setFont(SWTResourceManager.getFont(GUIConstants.fontName,
						GUIConstants.fontSize, 0, false, false));
			}
			{
				scanStatusTextArea =
						new Text(this, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				FormData scanStatusTextAreaLData = new FormData();
				scanStatusTextAreaLData.width = 706;
				scanStatusTextAreaLData.height = 282;
				scanStatusTextAreaLData.left = new FormAttachment(0, 1000, 13);
				scanStatusTextAreaLData.right = new FormAttachment(1000, 1000, 0);
				scanStatusTextAreaLData.bottom = new FormAttachment(1000, 1000, -80);
				scanStatusTextAreaLData.top = new FormAttachment(0, 1000, 16);
				scanStatusTextArea.setLayoutData(scanStatusTextAreaLData);
				scanStatusTextArea.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0,
						false, false));
			}

			this.layout();
	}

	/**
	 * Overriding checkSubclass allows this class to extend
	 * org.eclipse.swt.widgets.Composite
	 */
	@Override
	protected void checkSubclass()
	{
	}
	
	public void updateQueueSizes()
	{
		testerQueueSizeTextBox.setText(String.valueOf(Scan.getInstance().getTesterQueue().getQueueLength()));
		categorizerQueueSizeTextBox.setText(String.valueOf(Scan.getInstance().getCategorizerQueue().getQueueLength()));
		requesterQueueSizeTextBox.setText(String.valueOf(Scan.getInstance().getRequesterQueue().getQueueLength()));
	}

	protected int maxStatusSize = 100000;
	int statusMessageUpdateTime = 0;

	public synchronized void appendStatusText(String text)
	{
		if (!isDisposed())
		{
			getDisplay().asyncExec(new StatusMessage(text));
		}
	}

	private class StatusMessage implements Runnable
	{
		private String text;
		
		public StatusMessage(String text)
		{
			this.text = text;
		}
		
		@Override
		public void run()
		{
			long start = Calendar.getInstance().getTimeInMillis();
			scanStatusTextArea.append(text);
			String newText = scanStatusTextArea.getText();
			if (newText.length() > maxStatusSize * 2)
			{
				int startPosition = newText.length() - maxStatusSize;
				scanStatusTextArea.setText(newText.substring(startPosition));
			}
			long end = Calendar.getInstance().getTimeInMillis();
			statusMessageUpdateTime += end - start;
		}
	}

	public long getStatusMessageUpdateTime()
	{
		return statusMessageUpdateTime;
	}
}
