/**
 *
 */


fun main() {

    val b = System.currentTimeMillis()

    println(System.getProperties().getProperty("sun.desktop"))

    println(RunConfig.intArray)

    println(RunConfig.number)
    RunConfig.number = 2
    println(RunConfig.string)
    RunConfig.string = "aaa"
    println(RunConfig.nullableNumber)
    RunConfig.nullableNumber = 5

    print(RunConfig.nullableString)
    RunConfig.nullableString = "123"

    println(RunConfig.model)
    RunConfig.model = Model()
    println(RunConfig.model)

    RunConfig.clear()
    RunConfig.intArray = intArrayOf(2, 3, 4, 9)

    val e = System.currentTimeMillis()
    print(e - b)
}