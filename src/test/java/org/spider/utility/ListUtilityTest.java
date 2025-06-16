package org.spider.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ListUtilityTest {

	@Test
	public void toList() {
		assertEquals(List.of("a", "b", "c"), ListUtility.toList("a,b,c"));
		assertEquals(List.of(), ListUtility.toList(""));
		assertEquals(List.of(), ListUtility.toList(null));
	}

	@Test
	public void toStringTest() {
		assertEquals("a,b,c", ListUtility.toString(List.of("a", "b", "c")));
	}

	@Test
	public void formatList() {
		assertEquals("a, b, c", ListUtility.formatList(List.of("a", "b", "c")));
	}
}
