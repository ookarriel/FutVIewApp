# FutView — reglas R8 (release con minify)
#
# Firestore (CustomClassMapper) y Retrofit/Gson serializan por reflexión usando
# los nombres de campo de los DTOs: si R8 los renombra, el mapeo deja de funcionar.
# MapLibre, Retrofit y Firebase incluyen sus propias reglas de consumidor.

-keep class com.example.futview.data.** { *; }

# Evitar warnings de dependencias opcionales
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
