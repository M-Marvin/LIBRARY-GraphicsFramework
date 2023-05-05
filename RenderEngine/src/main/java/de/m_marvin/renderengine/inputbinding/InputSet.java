package de.m_marvin.renderengine.inputbinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A list of {@link IBinding}-sets describing multiple key-combinations for a key-binding.
 * 
 * @author Marvin Koehler
 */
public class InputSet {
	
	protected List<Set<IBinding>> inputSets = new ArrayList<>();
	
	/**
	 * Adds the given inputs as combination to this input-set.
	 * All of the given keys have to be pressed to make the set count as "pressed".
	 * 
	 * @param bindingCombi The {@link IBinding}s that have to be pressed at the same time in order to let this key-combination count as pressed.
	 * @return This input set to add more key-combinations
	 */
	public InputSet addBinding(IBinding... bindingCombi) {
		Set<IBinding> bindingSet = new HashSet<>();
		for (IBinding binding : bindingCombi) bindingSet.add(binding);
		inputSets.add(bindingSet);
		return this;
	}
	
	/**
	 * Removes the key-combination with the given index from this set.
	 * @implNote Calls remove on a ArrayList and throws exceptions if the index is out of bounds.
	 * 
	 * @param index The index of the combination to remove
	 * @return Returns a {@link Set} containing the {@link IBindings} that where contained within the key-combination.
	 */
	public Set<IBinding> removeBingingSet(int index) {
		return this.inputSets.remove(index);
	}
	
	/**
	 * Returns a {@link ArrayList} containing the {@link Set}s representing the key-combinations.
	 * @return A {@link ArrayList} containing multiple {@link Set}s representing the key-combinations.
	 */
	public List<Set<IBinding>> getBindingSets() {
		return inputSets;
	}
	
	/**
	 * Only returns true if all {@link IBinding}s of at least one of the key-combinations of this set are pressed.
	 * @param window The GLFW window handle id of the window to check for input
	 * @return True if at least one of the key-combinations is pressed
	 */
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
