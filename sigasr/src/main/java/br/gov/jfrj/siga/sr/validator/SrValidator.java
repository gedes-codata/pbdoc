package br.gov.jfrj.siga.sr.validator;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;

public class SrValidator {

	private List<SrError> errors = new ArrayList<>();

	public void addError(String key, String value){
		if(isNotBlank(key) || isNotBlank(value)) {
			errors.add(new SrError(key, value));
		}
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public List<SrError> getErros() {
		return errors;
	}
}
