package com.example.lifelineapp.model

class PatientData {
   companion object {
      var patientId: String = "default_patient"

      fun clearData() {
         patientId = "default_patient"
      }
   }
}
