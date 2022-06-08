import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Anagrams_Stream {
    public static void main(String[] args) {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

       try(Stream<String> words = Files.lines(dictionary)){
           words.collect(
               groupingBy(word -> word.chars().sorted()
													.collect(StringBuilder::new,
													(sb,c -> sb.append(char) c), 
													StringBuilder::append).toString()))
							 .values().stream()
							 .filter(group -> group.size() >= minGroupSize)
							 .map(group -> group.size() + ": " + group)
							 .forEach(System.out::println);
       }
    }
}