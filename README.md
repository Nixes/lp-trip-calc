## Structure

The project is structured as follows:
- `src/main/java/org/nixes/` contains the source code
- `src/test/java/` contains the tests

## Installation and Build

Depends on a java 21 JDK.

## Execution

Run 'TripProcessCommand' with an input taps.csv in the working directory.


## Assumptions

- The input file is in the correct format
- The input file is in the working directory
- The input file is named taps.csv
- The case of fields in the input file is consistent with the example provided in the task description
- The fare rule is as per the example provided in the task description
- Fare rules do not change.
- First tap is always a tap on
- Taps are in ascending order of DateTimeUTC
- Cancelled trips use the same from and to StopId
- Cancelled trips use the first DateTimeUTC as both the Started and Finished values