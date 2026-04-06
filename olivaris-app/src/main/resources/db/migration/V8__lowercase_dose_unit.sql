UPDATE phyto_activity
SET dose_unit = LOWER(dose_unit)
WHERE dose_unit IS NOT NULL
    AND dose_unit <> LOWER(dose_unit)