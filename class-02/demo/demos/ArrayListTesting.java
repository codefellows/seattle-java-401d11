import java.util.ArrayList;
public class ArrayListTesting {
  public static void main(String[] args) {
    System.out.println("running");
    ArrayList<String> arrayList = new ArrayList<>();
    arrayList.add("Hello");
    arrayList.add("World");
    for (String elt : arrayList) {
      System.out.println(elt);
    }
    arrayList.add("!");
    for (String elt : arrayList) {
      System.out.println(elt);
    }
  }
}