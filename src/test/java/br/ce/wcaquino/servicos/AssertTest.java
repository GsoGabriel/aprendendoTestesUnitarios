package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void test() {
		Assert.assertTrue(!false);
		Assert.assertFalse(!true);
		
		Assert.assertEquals("Erro de comparacao!", 1, 1);
		Assert.assertEquals(0.51, 0.51, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("eu");
		Usuario u2 = new Usuario("eu");
		Usuario u3 = null;
		Usuario u4 = u2;
		
		Assert.assertEquals(u1.toString(), u2.toString());
		
		Assert.assertSame(u2,u4);
		Assert.assertNotSame(u1,u4);
		
		Assert.assertNull(u3);
		Assert.assertNotNull(u1);
		
	}
}
