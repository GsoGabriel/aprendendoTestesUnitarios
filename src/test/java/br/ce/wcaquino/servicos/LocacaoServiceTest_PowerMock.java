package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.machers.MatchersProprios;
import br.ce.wcaquino.servicos.EmailService;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.SPCService;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class LocacaoServiceTest_PowerMock {
	
	@InjectMocks
	private LocacaoService service;
	@Mock
	private SPCService spcService;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector errors = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
//		service = new LocacaoService();
//		dao = Mockito.mock(LocacaoDAO.class);
//		service.setLocacaoDAO(dao);
//		spcService = Mockito.mock(SPCService.class);
//		service.setSPCService(spcService);
//		email = Mockito.mock(EmailService.class);
//		service.setEmailService(email);
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(120.00).agora(), umFilme().comValor(20.00).agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(27, 4, 2017));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 27);
//		calendar.set(Calendar.MONTH, Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//acao
		Locacao loc = service.alugarFilme(usuario, filmes);
		
		// verificacao
		errors.checkThat(loc.getValor(), is(equalTo(140.00)));
//		errors.checkThat(loc.getDataLocacao(), MatchersProprios.ehHoje());
//		errors.checkThat(loc.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(1));
		errors.checkThat(DataUtils.isMesmaData(loc.getDataLocacao(), DataUtils.obterData(27, 4, 2017)), is(true));
		errors.checkThat(DataUtils.isMesmaData(loc.getDataRetorno(), DataUtils.obterData(28, 4, 2017)), is(true));
//		PowerMockito.verifyStatic(Mockito.times(2));
//		Calendar.getInstance();
	}

	@Test
	public void naoDeveDevolverFilmeDomingo() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
//		List<Filme> filmes = Arrays.asList(new Filme("filme1", 2, 4.00));
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 29);
//		calendar.set(Calendar.MONTH, Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		
		// acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		// verificacao
//		assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
//		PowerMockito.verifyStatic(Mockito.times(2));
//		Calendar.getInstance();
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
	}
	
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
//		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		Assert.assertThat(locacao.getValor(), is(5.0));
//		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}
}
