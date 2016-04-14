package com.mzigman.vlsm.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mzigman.vlsm.Subnet;
import com.mzigman.vlsm.controllers.VLSMController;
import com.mzigman.vlsm.enums.Field;
import com.mzigman.vlsm.util.SpringUtilities;
import com.mzigman.vlsm.util.VLSMUtilities;

public class GUI extends JFrame {

	private VLSMController controller;

	private static final long serialVersionUID = 1L;
	private JTextField addressField;
	private JTextField subnetField;
	private List<LinkedHashMap<Field, Component>> componentHashList;

	public GUI(VLSMController controller) {		
		super("VLSM Calculator");
		this.controller = controller;
		setResizable(false);
		init();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void init(){
		add(createTopPanel(), BorderLayout.PAGE_START);
		pack();
	}

	private Component createTopPanel(){
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		addressField = new JTextField(12);
		subnetField = new JTextField(4);

		JButton enterButton = new JButton("Enter");
		enterButton.setActionCommand("Enter");
		enterButton.addActionListener(new ButtonClickListener());

		topPanel.add(new JLabel("IPv4 Address in CIDR Notation"));
		topPanel.add(addressField);
		topPanel.add(new JLabel("# of Subnets"));
		topPanel.add(subnetField);
		topPanel.add(enterButton);

		return topPanel;
	}

	private Component createMiddlePanel(){
		SpringLayout sl = new SpringLayout();
		JPanel middlePanel = new JPanel(sl);
		middlePanel.setName("middlePanel");
		int numOfRows = Integer.parseInt(subnetField.getText());
		componentHashList = new ArrayList<LinkedHashMap<Field, Component>>();

		JLabel[] headers = { new JLabel("#"), new JLabel("Size"), 
				new JLabel("Network Address"), new JLabel("Subnet Mask"), 
				new JLabel("Broadcast Address"), new JLabel("Host Pool Size"), 
				new JLabel("Host Pool Range") };

		for (int i = 0; i < headers.length; i++){
			middlePanel.add(headers[i]);
		}

		for (int i = 0; i < numOfRows; i++) {
			LinkedHashMap<Field, Component> hash = new LinkedHashMap<>();

			JLabel numberField = new JLabel("" + (i + 1));
			JTextField sizeField = new JTextField();
			JTextField networkAddressField = new JTextField();
			JTextField subnetMaskField = new JTextField();
			JTextField broadcastAddressField = new JTextField();
			JTextField hostPoolSizeField = new JTextField();
			JTextField hostPoolRangeField = new JTextField();

			sizeField.setPreferredSize(new Dimension(25, 2));
			subnetMaskField.setPreferredSize(new Dimension(100, 2));
			hostPoolRangeField.setPreferredSize(new Dimension(160, 2));

			networkAddressField.setEditable(false);
			subnetMaskField.setEditable(false);
			broadcastAddressField.setEditable(false);
			hostPoolSizeField.setEditable(false);
			hostPoolRangeField.setEditable(false);

			hash.put(Field.SEQUENCE, numberField);
			hash.put(Field.REQUESTED_HOSTS, sizeField);
			hash.put(Field.NETWORK_ADDRESS, networkAddressField);
			hash.put(Field.SUBNET_MASK, subnetMaskField);
			hash.put(Field.BROADCAST_ADDRESS, broadcastAddressField);
			hash.put(Field.HOST_POOL_SIZE, hostPoolSizeField);
			hash.put(Field.HOST_POOL_RANGE, hostPoolRangeField);

			componentHashList.add(hash);

			//subnetFields[1].setPreferredSize(new Dimension(3, 3));

			for (Component c : hash.values()) {
				middlePanel.add(c);
			}
		}

		SpringUtilities.makeCompactGrid(middlePanel,
				numOfRows + 1, headers.length, //rows, columns
				5, 5, //initialX, initialY
				5, 5);//xPad, yPad

		//middlePanel.setPreferredSize(this.getMaximumSize());
		return new JScrollPane(middlePanel);
	}

	private Component createBottomPanel(){
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton calculateButton = new JButton("Calculate");
		calculateButton.setActionCommand("Calculate");
		calculateButton.addActionListener(new ButtonClickListener());

		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("Clear");
		clearButton.addActionListener(new ButtonClickListener());

		JButton exportButton = new JButton("Export");
		exportButton.setActionCommand("Export");
		exportButton.addActionListener(new ButtonClickListener());

		bottomPanel.add(calculateButton);
		bottomPanel.add(clearButton);
		bottomPanel.add(exportButton);

		return bottomPanel;
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			if (command.equals("Enter")){
				if (controller.validateAddressField(addressField.getText()) 
						&& controller.validateSubnetField(subnetField.getText())){
					controller.setAddress(addressField.getText());
					add(createMiddlePanel(), BorderLayout.CENTER);
					add(createBottomPanel(), BorderLayout.PAGE_END);
					pack();
				}
				else {
					JOptionPane.showMessageDialog(getParent(), "Enter a valid CIDR address.");
					controller.resetVLSM();
				}
			}
			else if (command.equals("Calculate")) {
				List<Subnet> subnetList = new ArrayList<>();
				try{
					for (int i = 0; i < componentHashList.size(); i++){
						int requestedHosts = extractComponentValue(componentHashList, i, Field.REQUESTED_HOSTS);
						subnetList.add(new Subnet(i, requestedHosts));
					}
				} catch (Exception e1){
					JOptionPane.showMessageDialog(getParent(), "Enter all subnet sizes.");
					return;
				}

				if (controller.assignSubnets(subnetList)){
					subnetList = controller.getUpdatedSubnets();
					for (int i = 0; i < subnetList.size(); i++){
						insertComponentValue(componentHashList, subnetList.get(i), i, Field.BROADCAST_ADDRESS);
						insertComponentValue(componentHashList, subnetList.get(i), i, Field.SUBNET_MASK);
						insertComponentValue(componentHashList, subnetList.get(i), i, Field.NETWORK_ADDRESS);
						insertComponentValue(componentHashList, subnetList.get(i), i, Field.HOST_POOL_RANGE);
						insertComponentValue(componentHashList, subnetList.get(i), i, Field.HOST_POOL_SIZE);
					}	
				}
				else{
					JOptionPane.showMessageDialog(getParent(), "Subnet will not fit in space given.");
				}
			}
			else if (command.equals("Export")) {
				JFileChooser c = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File", "txt");
				c.setFileFilter(filter);
				int returnVal = c.showSaveDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File f = c.getSelectedFile();
					controller.exportToFile(f);
					JOptionPane.showMessageDialog(getParent(), "Information has been exported to a file.");
				}
			}
			else if (command.equals("Clear")) {
				controller.resetVLSM();
				getContentPane().removeAll();
				getContentPane().validate();
				getContentPane().repaint();
				init();
			}
		}	
	}

	private int extractComponentValue(List<LinkedHashMap<Field, Component>> componentHashList,
			int index, Field fieldType){
		if (index >= componentHashList.size()){
			throw new IndexOutOfBoundsException();
		}
		Component temp = componentHashList.get(index).get(fieldType);
		if (fieldType == Field.SEQUENCE){
			return Integer.parseInt(((JLabel)temp).getText());
		}
		else{
			return Integer.parseInt(((JTextField)temp).getText());
		}
	}

	private void insertComponentValue(List<LinkedHashMap<Field, Component>> componentHashList,
			Subnet subnet, int index, Field fieldType){
		if (index >= componentHashList.size()){
			throw new IndexOutOfBoundsException();
		}
		int value;
		int value2 = 0;
		switch (fieldType){
		case BROADCAST_ADDRESS:
			value = subnet.getBroadcastAddress();
			break;
		case HOST_POOL_END:
			value = subnet.getHostPoolEnd();
			break;
		case HOST_POOL_SIZE:
			value = subnet.getHostPoolSize();
			break;
		case NETWORK_ADDRESS:
			value = subnet.getNetworkAddress();
			break;
		case SEQUENCE:
			value = subnet.getSequence();
			break;
		case REQUESTED_HOSTS:
			value = subnet.getRequestedHosts();
			break;
		case SUBNET_MASK:
			value = subnet.getSubnetMask();
			break;
		case SLASH:
			value = subnet.getSlash();
			break;
		case HOST_POOL_RANGE:
			value = subnet.getHostPoolBegin();
			value2 = subnet.getHostPoolEnd();
			break;
		default:
			throw new IndexOutOfBoundsException();
		}
		Component temp = componentHashList.get(index).get(fieldType);
		if (fieldType == Field.HOST_POOL_RANGE){
			String holyFuckThisCodeIsAwful = VLSMUtilities.addressToString(value) + " - " + VLSMUtilities.addressToString(value2);
			((JTextField)temp).setText(holyFuckThisCodeIsAwful);
		}
		else if (fieldType == Field.HOST_POOL_SIZE){
			((JTextField)temp).setText("" + value);
		}
		else{
			((JTextField)temp).setText(VLSMUtilities.addressToString(value));
		}
	}
}
