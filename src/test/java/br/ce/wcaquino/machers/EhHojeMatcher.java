package br.ce.wcaquino.machers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class EhHojeMatcher extends TypeSafeMatcher<Date> {
	
	private Integer diferen�aDias;

	public EhHojeMatcher(Integer diferen�aDias) {
		this.diferen�aDias = diferen�aDias;
	}

	public void describeTo(Description description) {
		Date dataEsperada = DataUtils.obterDataComDiferencaDias(diferen�aDias);
		DateFormat format = new SimpleDateFormat("dd/MM/YYY");
		description.appendText(format.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(diferen�aDias));
	}

}
