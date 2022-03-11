package cc.oxie.morsebuzzer

val rawAlphabet = hashMapOf(
    'A' to arrayOf(1, 3),
    'B' to arrayOf(3, 1, 1, 1),
    'C' to arrayOf(3, 1, 3, 1),
    'D' to arrayOf(3, 1, 1),
    'E' to arrayOf(1),
    'F' to arrayOf(1, 1, 3, 1),
    'G' to arrayOf(3, 3, 1),
    'H' to arrayOf(1, 1, 1, 1),
    'I' to arrayOf(1, 1),
    'J' to arrayOf(1, 3, 3, 3),
    'K' to arrayOf(3, 1, 3),
    'L' to arrayOf(1, 3, 1, 1),
    'M' to arrayOf(3, 3),
    'N' to arrayOf(3, 1),
    'O' to arrayOf(3, 3, 3),
    'P' to arrayOf(1, 3, 3, 1),
    'Q' to arrayOf(3, 3, 1, 3),
    'R' to arrayOf(1, 3, 1),
    'S' to arrayOf(1, 1, 1),
    'T' to arrayOf(3),
    'U' to arrayOf(1, 1, 3),
    'V' to arrayOf(1, 1, 1, 3),
    'W' to arrayOf(1, 3, 3),
    'X' to arrayOf(3, 1, 1, 3),
    'Y' to arrayOf(3, 1, 3, 3),
    'Z' to arrayOf(3, 3, 1, 1),
)

/**
 * Add a time off of 1 between each sound
 */
val alphabet = rawAlphabet.mapValues { entry ->
    Array(entry.value.size * 2 - 1) { k ->
        if (k % 2 == 0) {
            entry.value[k/2]
        } else {
            1
        }
    }
}

// Dit: 1 unit
// Dah: 3 units
// Intra-character space: 1 unit
// Inter-character space: 3 units
// Word space: 7 units

/**
 * Produce a sequence alternating timings for silence and noise for
 * the morse code of the given string
 */
fun stringToMorseTimingArray(input: String): IntArray {
    val text = input.uppercase()
    // compute the result array size
    var size = 0
    text.forEach { character ->
        size += 1 + (alphabet[character]?.size ?: 3)
    }

    // fill the result array
    var index = 0
    val array = IntArray(size)
    text.forEach { character ->
        // insert a pause of 3 at the beginning of each character
        array[index] = 3
        index += 1
        val character = alphabet[character] ?: arrayOf(0, 4, 0)
        character.forEach { duration ->
            array[index] = duration
            index += 1
        }
    }
    // remove the pause of 3 at the beginning of the first character
    array[0] = 0
    return array
}

/**
 * Produce an int array of the given size, containing alternating values of 0 and 255
 */
fun createAmplitudeArray(size: Int): IntArray {
    val array = IntArray(size)
    (0 until size).map {
        array[it] = 255 * (it % 2)
    }
    return array
}
