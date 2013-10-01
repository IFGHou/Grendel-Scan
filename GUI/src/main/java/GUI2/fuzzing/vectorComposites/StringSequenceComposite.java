package com.grendelscan.GUI2.fuzzing.vectorComposites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import com.grendelscan.GUI.customControls.basic.GGroup;
import com.grendelscan.GUI.customControls.basic.GLabel;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GUI.Verifiers.EnforceIntegersOnly;
import com.grendelscan.fuzzing.FuzzVector;
import com.grendelscan.fuzzing.FuzzVectorFormatException;
import com.grendelscan.fuzzing.StringSequence;
import com.grendelscan.logging.Log;

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
public class StringSequenceComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements FuzzVectorComposite
{
	private GGroup charactersGroup;
	GText hexCharactersTextbox;
	private GText textCharactersTextBox;
	private GText maxDigitsTextbox;
	private GLabel maxDigitsLabel;
	private GText minDigitsTextbox;
	private GLabel minDigitsLabel;
	Combo definedCombo;
	private GLabel textCharactersLabel;
	private GLabel hexCharactersLabel;
	Map<String, Preset> presets;
	boolean changing = false;

	public StringSequenceComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
			this.setLayout(new FormLayout());
			EnforceIntegersOnly integersOnly = new EnforceIntegersOnly();  
			{
				minDigitsTextbox = new GText(this, SWT.BORDER);
				FormData minDigitsTextboxLData = new FormData();
				minDigitsTextboxLData.width = 40;
				minDigitsTextboxLData.height = 20;
				minDigitsTextboxLData.left =  new FormAttachment(0, 1000, 110);
				minDigitsTextboxLData.top =  new FormAttachment(0, 1000, 0);
				minDigitsTextbox.setLayoutData(minDigitsTextboxLData);
				minDigitsTextbox.setText("1");
				minDigitsTextbox.addVerifyListener(integersOnly);
			}
			{
				maxDigitsTextbox = new GText(this, SWT.BORDER);
				FormData text1LData1 = new FormData();
				text1LData1.width = 40;
				text1LData1.height = 20;
				text1LData1.left =  new FormAttachment(0, 1000, 280);
				text1LData1.top =  new FormAttachment(0, 1000, 0);
				maxDigitsTextbox.setLayoutData(text1LData1);
				maxDigitsTextbox.setText("1");
				maxDigitsTextbox.addVerifyListener(integersOnly);
			}
			{
				minDigitsLabel = new GLabel(this, SWT.NONE);
				FormData minDigitsLData = new FormData();
				minDigitsLData.width = 102;
				minDigitsLData.height = 17;
				minDigitsLData.left =  new FormAttachment(0, 1000, 0);
				minDigitsLData.top =  new FormAttachment(0, 1000, 3);
				minDigitsLabel.setLayoutData(minDigitsLData);
				minDigitsLabel.setText("Minimum length:");
			}
			{
				charactersGroup = new GGroup(this, SWT.NONE);
				FormLayout charactersGroupLayout = new FormLayout();
				charactersGroup.setLayout(charactersGroupLayout);
				FormData charactersGroupLData = new FormData();
				charactersGroupLData.width = 400;
				charactersGroupLData.height = 284;
				charactersGroupLData.left =  new FormAttachment(0, 1000, 0);
				charactersGroupLData.top =  new FormAttachment(0, 1000, 34);
				charactersGroup.setLayoutData(charactersGroupLData);
				charactersGroup.setText("Character Set");
				{
					FormData definedComboLData = new FormData();
					definedComboLData.width = 344;
					definedComboLData.height = 25;
					definedComboLData.left =  new FormAttachment(0, 1000, 5);
					definedComboLData.top =  new FormAttachment(0, 1000, 5);
					definedCombo = new Combo(charactersGroup, SWT.READ_ONLY);
					definedCombo.setLayoutData(definedComboLData);
					definedCombo.addModifyListener(new ModifyListener() 
					{
						@Override
						public void modifyText(ModifyEvent evt) 
						{
							if (presets.containsKey(definedCombo.getText()))
							{
								changing = true;
								Preset preset = presets.get(definedCombo.getText());
								textCharactersTextBox.setText(preset.text);
								hexCharactersTextbox.setText(preset.hex);
								changing = false;
							}
						}
					});
				}
				{
					FormData textCharactersTextBoxLData = new FormData();
					textCharactersTextBoxLData.width = 349;
					textCharactersTextBoxLData.height = 67;
					textCharactersTextBoxLData.left =  new FormAttachment(0, 1000, 5);
					textCharactersTextBoxLData.top =  new FormAttachment(0, 1000, 77);
					textCharactersTextBox = new GText(charactersGroup, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
					textCharactersTextBox.setLayoutData(textCharactersTextBoxLData);
					textCharactersTextBox.addModifyListener(new ModifyListener() 
					{
						@Override
						public void modifyText(ModifyEvent evt) 
						{
							if (!changing)
								definedCombo.setText(CUSTOM);
						}
					});
				}
				{
					FormData text1LData = new FormData();
					text1LData.width = 349;
					text1LData.height = 61;
					text1LData.left =  new FormAttachment(0, 1000, 5);
					text1LData.top =  new FormAttachment(0, 1000, 196);
					hexCharactersTextbox = new GText(charactersGroup, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
					hexCharactersTextbox.setLayoutData(text1LData);
					hexCharactersTextbox.addModifyListener(new ModifyListener() 
					{
						@Override
						public void modifyText(ModifyEvent evt) 
						{
							if (!changing)
								definedCombo.setText(CUSTOM);
						}
					});
				}
				{
					hexCharactersLabel = new GLabel(charactersGroup, SWT.NONE);
					FormData hexCharactersLabelLData = new FormData();
					hexCharactersLabelLData.width = 101;
					hexCharactersLabelLData.height = 17;
					hexCharactersLabelLData.left =  new FormAttachment(0, 1000, 5);
					hexCharactersLabelLData.top =  new FormAttachment(0, 1000, 167);
					hexCharactersLabel.setLayoutData(hexCharactersLabelLData);
					hexCharactersLabel.setText("Hex Characters:");
				}
				{
					textCharactersLabel = new GLabel(charactersGroup, SWT.NONE);
					FormData textCharactersLabelLData = new FormData();
					textCharactersLabelLData.width = 109;
					textCharactersLabelLData.height = 17;
					textCharactersLabelLData.left =  new FormAttachment(0, 1000, 5);
					textCharactersLabelLData.top =  new FormAttachment(0, 1000, 48);
					textCharactersLabel.setLayoutData(textCharactersLabelLData);
					textCharactersLabel.setText("GText Characters:");
				}
			}
			{
				maxDigitsLabel = new GLabel(this, SWT.NONE);
				FormData label1LData = new FormData();
				label1LData.width = 102;
				label1LData.height = 17;
				label1LData.left =  new FormAttachment(0, 1000, 174);
				label1LData.top =  new FormAttachment(0, 1000, 3);
				maxDigitsLabel.setLayoutData(label1LData);
				maxDigitsLabel.setText("Maximum length:");
			}
			createPresets();
			this.layout();
	}
	
	private class Preset
	{
		public Preset(String description, String hex, String text)
        {
	        this.hex = hex;
	        this.text = text;
	        this.description = description;
        }

		String description, hex, text;
	}
	
	
	private void createPresets()
	{
		List<Preset>tmpPresets = new ArrayList<Preset>(13);
		tmpPresets.add(new Preset("All ASCII characters - 0x00-0xff", "0x00 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x09 0x0a 0x0b 0x0c 0x0d 0x0e 0x0f 0x10 0x11 0x12 0x13 0x14 0x15 0x16 0x17 0x18 0x19 0x1a 0x1b 0x1c 0x1d 0x1e 0x1f 0x20 0x21 0x22 0x23 0x24 0x25 0x26 0x27 0x28 0x29 0x2a 0x2b 0x2c 0x2d 0x2e 0x2f 0x30 0x31 0x32 0x33 0x34 0x35 0x36 0x37 0x38 0x39 0x3a 0x3b 0x3c 0x3d 0x3e 0x3f 0x40 0x41 0x42 0x43 0x44 0x45 0x46 0x47 0x48 0x49 0x4a 0x4b 0x4c 0x4d 0x4e 0x4f 0x50 0x51 0x52 0x53 0x54 0x55 0x56 0x57 0x58 0x59 0x5a 0x5b 0x5c 0x5d 0x5e 0x5f 0x60 0x61 0x62 0x63 0x64 0x65 0x66 0x67 0x68 0x69 0x6a 0x6b 0x6c 0x6d 0x6e 0x6f 0x70 0x71 0x72 0x73 0x74 0x75 0x76 0x77 0x78 0x79 0x7a 0x7b 0x7c 0x7d 0x7e 0x7f 0x80 0x81 0x82 0x83 0x84 0x85 0x86 0x87 0x88 0x89 0x8a 0x8b 0x8c 0x8d 0x8e 0x8f 0x90 0x91 0x92 0x93 0x94 0x95 0x96 0x97 0x98 0x99 0x9a 0x9b 0x9c 0x9d 0x9e 0x9f 0xa0 0xa1 0xa2 0xa3 0xa4 0xa5 0xa6 0xa7 0xa8 0xa9 0xaa 0xab 0xac 0xad 0xae 0xaf 0xb0 0xb1 0xb2 0xb3 0xb4 0xb5 0xb6 0xb7 0xb8 0xb9 0xba 0xbb 0xbc 0xbd 0xbe 0xbf 0xc0 0xc1 0xc2 0xc3 0xc4 0xc5 0xc6 0xc7 0xc8 0xc9 0xca 0xcb 0xcc 0xcd 0xce 0xcf 0xd0 0xd1 0xd2 0xd3 0xd4 0xd5 0xd6 0xd7 0xd8 0xd9 0xda 0xdb 0xdc 0xdd 0xde 0xdf 0xe0 0xe1 0xe2 0xe3 0xe4 0xe5 0xe6 0xe7 0xe8 0xe9 0xea 0xeb 0xec 0xed 0xee 0xef 0xf0 0xf1 0xf2 0xf3 0xf4 0xf5 0xf6 0xf7 0xf8 0xf9 0xfa 0xfb 0xfc 0xfd 0xfe 0xff", ""));
		tmpPresets.add(new Preset("Letters only - lowercase ", "", "abcdefghijklmnopqrstuvwxyz"));
		tmpPresets.add(new Preset("Letters only - uppercase", "", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		tmpPresets.add(new Preset("Letters only - mixed case", "", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		tmpPresets.add(new Preset("Alphanumeric - lowercase ", "", "abcdefghijklmnopqrstuvwxyz0123456789"));
		tmpPresets.add(new Preset("Alphanumeric only - uppercase", "", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
		tmpPresets.add(new Preset("Alphanumeric only - mixed case", "", "abcdefghijklmnopqrstuvwxyABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
		tmpPresets.add(new Preset("Numbers only - decimal", "", "0123456789"));
		tmpPresets.add(new Preset("Numbers only - hexadecimal", "", "0123456789abcdef"));
		tmpPresets.add(new Preset("Keyboard characters - non-alphanumeric", "", "`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?"));
		tmpPresets.add(new Preset("Keyboard characters - all", "", "abcdefghijklmnopqrstuvwxyABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?"));
		tmpPresets.add(new Preset("Extended characters - 0x00-0x1f", "0x00 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x09 0x0a 0x0b 0x0c 0x0d 0x0e 0x0f 0x10 0x11 0x12 0x13 0x14 0x15 0x16 0x17 0x18 0x19 0x1a 0x1b 0x1c 0x1d 0x1e 0x1f", ""));
		tmpPresets.add(new Preset("Extended characters - 0x7f-0xff", "0x7f 0x80 0x81 0x82 0x83 0x84 0x85 0x86 0x87 0x88 0x89 0x8a 0x8b 0x8c 0x8d 0x8e 0x8f 0x90 0x91 0x92 0x93 0x94 0x95 0x96 0x97 0x98 0x99 0x9a 0x9b 0x9c 0x9d 0x9e 0x9f 0xa0 0xa1 0xa2 0xa3 0xa4 0xa5 0xa6 0xa7 0xa8 0xa9 0xaa 0xab 0xac 0xad 0xae 0xaf 0xb0 0xb1 0xb2 0xb3 0xb4 0xb5 0xb6 0xb7 0xb8 0xb9 0xba 0xbb 0xbc 0xbd 0xbe 0xbf 0xc0 0xc1 0xc2 0xc3 0xc4 0xc5 0xc6 0xc7 0xc8 0xc9 0xca 0xcb 0xcc 0xcd 0xce 0xcf 0xd0 0xd1 0xd2 0xd3 0xd4 0xd5 0xd6 0xd7 0xd8 0xd9 0xda 0xdb 0xdc 0xdd 0xde 0xdf 0xe0 0xe1 0xe2 0xe3 0xe4 0xe5 0xe6 0xe7 0xe8 0xe9 0xea 0xeb 0xec 0xed 0xee 0xef 0xf0 0xf1 0xf2 0xf3 0xf4 0xf5 0xf6 0xf7 0xf8 0xf9 0xfa 0xfb 0xfc 0xfd 0xfe 0xff", ""));
		
		definedCombo.add(CUSTOM);
		presets = new HashMap<String, Preset>(13);
		for (Preset preset: tmpPresets)
		{
			definedCombo.add(preset.description);
			presets.put(preset.description, preset);
		}
		definedCombo.setText(CUSTOM);
	}
	
	private static final String CUSTOM = "Custom";

	@Override
	public FuzzVector getFuzzVector() throws FuzzVectorFormatException
    {
		int max = Integer.valueOf(maxDigitsTextbox.getText());
		int min = Integer.valueOf(minDigitsTextbox.getText());
		if (min > max)
		{
			throw new FuzzVectorFormatException("Minimum string length cannot be greater than maximum length.");
		}
		
		HashSet<String> patterns = new HashSet<String>();
		
		final String value = textCharactersTextBox.getText();
		for (int index = 0; index < value.length(); index++)
        {
			patterns.add(String.valueOf(value.charAt(index)));
        }

		for (String pattern: hexCharactersTextbox.getText().split("\\s+"))
        {
			
			if (pattern.length() > 0)
			{
				pattern = pattern.replace("0x", "");
				try
				{
					patterns.add(String.valueOf(Character.toChars(Integer.parseInt(pattern, 16))[0]));
				}
				catch (NumberFormatException e) 
				{
					Log.error("Bad hex pattern (" + pattern + "): " + e.toString(), e);
				}
			}
        }
		
		char chars[] = new char[patterns.size()];
		int index = 0;
		for (String pattern: patterns)
		{
			chars[index++] = pattern.charAt(0);
		}
	
	    return new StringSequence(chars, max, min);
    }

	@Override
	public void displayFuzzVector(FuzzVector oldVector)
    {
		hexCharactersTextbox.setText("");
		textCharactersTextBox.setText("");
		
		StringSequence vector = (StringSequence) oldVector;
		String hexText = "";
		String text = "";
		for (char c: vector.getCharacters())
		{
			if (c <= 32 || c >= 127)
			{
				hexText += "0x" + String.format("%02X", Integer.valueOf(c)) + " ";
				
			}
			else
			{
				text += String.valueOf(c);
				
			}
		}
		
		hexCharactersTextbox.setText(hexText);
		textCharactersTextBox.setText(text);
		
		maxDigitsTextbox.setText(String.valueOf(vector.getMaxDigits()));
		minDigitsTextbox.setText(String.valueOf(vector.getMinDigits()));
    }

	@Override
	public String getDescription()
    {
	    return "Character sequence";
    }

	@Override
	public Class getFuzzVectorClass()
    {
	    return StringSequence.class;
    }

}
