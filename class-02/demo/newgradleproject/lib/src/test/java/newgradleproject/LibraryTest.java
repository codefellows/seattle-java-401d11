/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package newgradleproject;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    @Test
    void Summing_two_numbers()
    {
        Library sut = new Library();

        final int FirstInt = 3;
        int actualSummedInt = sut.sumInts(FirstInt, 4);
        assertEquals(7, actualSummedInt, "Summed integers were incorrect! ");

        ArrayList<Integer> numArray = new ArrayList<>(1);
        numArray.add(3);
        numArray.add(4);
        numArray.remove(1);
        System.out.println(numArray);
    }
}
