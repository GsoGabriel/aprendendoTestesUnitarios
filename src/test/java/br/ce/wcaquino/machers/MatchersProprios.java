package br.ce.wcaquino.machers;

import java.util.Calendar;
import java.util.Date;

import br.ce.wcaquino.utils.DataUtils;

public class MatchersProprios {
	
	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static EhHojeMatcher ehHoje() {
		return ehHojeComDiferencaDias(0);
	}
	public static EhHojeMatcher ehHojeComDiferencaDias(Integer diferencaDias) {
		return new EhHojeMatcher(diferencaDias);
	}
	

}
