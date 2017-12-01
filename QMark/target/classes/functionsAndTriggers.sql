-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION `qmark`.`computePowerRate` (theTestCaseID LONG, theTestRunID LONG)
RETURNS DOUBLE
BEGIN

DECLARE lowerBound, upperBound, minID, maxID LONG;
DECLARE lowerBoundConsumption, upperBoundConsumption, middlePartConsumption, avgPowerRateOut DOUBLE;

SELECT avgPowerRate
FROM TestCase
WHERE testCaseID = theTestCaseID
INTO avgPowerRateOut;

IF (avgPowerRateOUT IS NULL) THEN
	SELECT `start` FROM testCase WHERE testCaseID = theTestCaseID INTO lowerBound;
	SELECT `stop` FROM testCase WHERE testCaseID = theTestCaseID INTO upperBound;

	SELECT powerRateID
	FROM resultpowerrate 
	WHERE testRunID = theTestRunID 
		AND time >= lowerBound
	ORDER BY powerRateID ASC
	LIMIT 0,1
	INTO minID;

	SELECT powerRateID
	FROM resultpowerrate 
	WHERE testRunID = theTestRunID 
		AND time <= upperBound
	ORDER BY powerRateID DESC
	LIMIT 0,1
	INTO maxID;

	SELECT power1 + (power2 - power1) * ((time1 - lowerBound) / (time1 - time2))
	FROM 
		(SELECT `power` AS power1, `time` AS time1 FROM resultpowerrate WHERE powerRateID = minID) `first`,
		(SELECT `power` AS power2, `time` AS time2  FROM resultpowerrate WHERE powerRateID = (minID -1)) `second`
	INTO lowerBoundConsumption;

	SELECT power1 + (power2 - power1) * ((upperBound - time1) / (time2 - time1))
	FROM
		(SELECT `power` AS power1, `time` AS time1 FROM resultpowerrate WHERE powerRateID = (maxId + 1)) `first`,
		(SELECT `power`AS power2, `time`AS time2 FROM resultpowerrate WHERE powerRateID = maxId) `second`
	INTO upperBoundConsumption;

	SELECT SUM((second.power + first.power) * (second.time - first.time) / 2) AS consumption
	FROM
		(
			SELECT powerRateID, -power AS power, time
			FROM resultpowerrate
			WHERE testRunID = theTestRunID
				AND powerRateID >= (minID - 1)
				AND powerRateID <= maxID
		) first,
		(
			SELECT powerRateID, -power AS power, time
			FROM resultpowerrate
			WHERE testRunID = theTestRunID
				AND powerRateID >= minID
				AND powerRateID <= (maxID + 1)
		) second
	WHERE first.powerRateID = second.powerRateID - 1
	INTO middlePartConsumption;

	SELECT (lowerBoundConsumption + upperBoundConsumption + middlePartConsumption) 
			/ (upperBound - lowerBound)
	INTO avgPowerRateOut;
END IF;

RETURN avgPowerRateOut;
END;


CREATE TRIGGER completeTestCase
AFTER UPDATE ON TestCase
FOR EACH ROW
SET avgPowerRate = computePowerRate(testCaseID, testRunID)