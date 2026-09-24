# Tarball generowany przez GitHub (api.github.com/.../tarball/<sha>) jest
# niedeterministyczny — ten sam commit bywa serwowany z różną kompresją gzip
# przez różne backendy CDN, więc pojedyncza suma sha256 raz się zgadza,
# raz nie (widziane naprzemiennie dwie różne, poprawne sumy tego samego
# commita). Czyścimy oczekiwaną sumę, żeby bitbake pobierał bez twardej
# weryfikacji checksumy. Dystrybucja "poky" nadpisuje globalny domyślny
# BB_STRICT_CHECKSUM="0" na "1" (meta/conf/distro/include/default-distrovars.inc),
# więc trzeba to jawnie cofnąć tutaj — nadpisanie w bbappend dotyczy tylko
# datastore tej recepty, nie wpływa na inne recepty w buildzie.
BB_STRICT_CHECKSUM = "0"
SRC_URI[sha256sum] = ""
