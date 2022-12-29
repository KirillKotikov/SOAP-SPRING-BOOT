SELECT equipmentId, addressId
    FROM incident
        WHERE incidentId = %d
            AND changeDate = '%s'