package de.m_marvin.renderengine.inputbinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputSet {
	
	protected List<Set<IBinding>> inputSets = new ArrayList<>();
	
	public InputSet addBinding(IBinding... bindingCombi) {
		Set<IBinding> bindingSet = new HashSet<>();
		for (IBinding binding : bindingCombi) bindingSet.add(binding);
		inputSets.add(bindingSet);
		return this;
	}
	
	public Set<IBinding> removeBingingSet(int index) {
		return this.inputSets.remove(index);
	}
	
	public List<Set<IBinding>> getBindingSets() {
		return inputSets;
	}
	
	public boolean isActive(long window) {
 		for (Set<IBinding> inputSet : this.inputSets) {
			boolean allActive = true;
			for (IBinding input : inputSet) {
				if (!input.isPressed(window)) {
					allActive = false;
					break;
				}
			}
			if (allActive) return true;
		}
		return false;
	}
	
}
