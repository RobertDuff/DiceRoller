package duffrd.diceroller.model;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class Experiment
{
	@Test
	public void test() throws IOException
	{
		List<Integer> x = IntStream.of ( new int[] { 1 ,2 ,3, 4, 5 } ).boxed ().collect ( Collectors.toList () );
		
		Yaml yaml = new Yaml();
		
		String text = yaml.dumpAll ( x.iterator () );
		
		System.out.println ( text );
	}
}
