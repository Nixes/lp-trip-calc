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
- Fare rules do not change during the course of processing a file.
- Cancelled/incomplete trips use the same from and to StopId
- Cancelled/incomplete trips use the first DateTimeUTC as both the Started and Finished values
- Assumed the names of the various trip statuses are "COMPLETED", "INCOMPLETE", "CANCELLED". Only COMPLETED was provided.
- Output trips.csv is sorted by Finished time

## Issues/Observations

- The DurationSecs in the example output seems incorrect. The difference in mins between the Started and Finished times is 5 mins which is 300 seconds. The originally provided output says 900 seconds.
- The started and finished times in the example output do not match the relevant tap ids in the input taps.csv file
- There are some cases of tap offs without tap ons in the example data. I have assumed that these should be considered as incomplete trips. But this would need to be confirmed with the business.
- What is the cause of these isolated tapOffs, is there a data quality/consistency issue?