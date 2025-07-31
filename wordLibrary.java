import java.util.Random;

public class wordLibrary {
    private static final String[] words = {
        "apple", "breeze", "candle", "dream", "echo", "flame", "grace", "horizon",
        "island", "jungle", "kindness", "laughter", "moment", "nature", "ocean",
        "peace", "quest", "river", "sunlight", "tranquil", "unity", "valor",
        "whisper", "xenial", "yearn", "zenith"
    };

    private static final Random rand = new Random();

    public static String getRandomWord() {
        int index = rand.nextInt(words.length);
        return words[index];
    }

    public static String[] getAllWords() {
        return words;
    }
}
