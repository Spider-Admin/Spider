package org.spider.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class NormalizeTest {

	@Test
	public void string() {
		assertEquals("abc", Normalize.string(" abc "));
		assertEquals("a b c", Normalize.string(" a  b   c    "));
	}

	@Test
	public void stringList() {
		assertEquals(List.of("a", "b", "c"), Normalize.stringList(" a, b ,c"));
		assertEquals(List.of("a", "b", "c"), Normalize.stringList(" a; b ;c"));
		assertEquals(List.of("a", "b", "c"), Normalize.stringList(" a" + "\n" + " b " + "\n" + "c"));
		assertEquals(List.of("a", "b", "c"), Normalize.stringList("a b c"));
	}
}
