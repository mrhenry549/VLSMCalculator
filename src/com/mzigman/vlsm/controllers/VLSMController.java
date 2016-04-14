package com.mzigman.vlsm.controllers;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import com.mzigman.vlsm.Subnet;
import com.mzigman.vlsm.VLSM;
import com.mzigman.vlsm.ui.GUI;

public class VLSMController {

	private VLSM vlsm;
	
	public VLSMController(VLSM vlsm){
		this.vlsm = vlsm;
		
		VLSMController thisController = this;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI gui = new GUI(thisController);
				gui.setVisible(true);
			}
		});
	}
	
	public boolean setAddress(String address){
		return vlsm.setAddress(address);
	}
	
	public void resetVLSM(){
		vlsm.reset();
	}

	public void exportToFile(File file) {
		vlsm.exportToFile(file);
	}

	public boolean assignSubnets(List<Subnet> subnetList) {
		return vlsm.assignSubnets(subnetList);
	}
	
	public boolean validateAddressField(String address){
		return Pattern.matches("(\\d+).(\\d+).(\\d+).(\\d+)/(\\d+)", address);
	}
	
	public boolean validateSubnetField(String subnet){
		try{
			int subInt = Integer.parseInt(subnet);
			return subInt > 0 && subInt < 100;
		} catch (Exception e) {}
		return false;
	}

	public List<Subnet> getUpdatedSubnets() {
		return vlsm.getSubnetList();
	}
}
