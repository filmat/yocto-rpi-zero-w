# Soundfont TimGM6mb (General MIDI, ok. 6 MB) autorstwa Tima Brechbilla,
# wcześniej dołączany do MuseScore. Autor: część próbek jest jego własna,
# reszta należy do domeny publicznej albo podlega GNU GPL. Licencja pliku
# według pliku copyright z repozytorium: GPL wersja 2.
# Upstream nie ma numeru wersji, więc wersja 1.0 jest umowna, a plik jest
# przypięty do commita d6ad4ed72dce1fd3d67f17b74e08cd7ae7941a96 i sumy
# sha256 poniżej.

SUMMARY = "Soundfont TimGM6mb do fluidsynth"
DESCRIPTION = "Basic sound font for fluidsynth from TimGM6mb"
HOMEPAGE = "https://github.com/arbruijn/TimGM6mb"
SECTION = "multimedia"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "https://raw.githubusercontent.com/arbruijn/TimGM6mb/d6ad4ed72dce1fd3d67f17b74e08cd7ae7941a96/TimGM6mb.sf2"
SRC_URI[md5sum] = "1f1ad87ae6f87033d9a591eca567d919"
SRC_URI[sha256sum] = "c5378b62028c920cb11e4803327983fee2f2cdff5dc89c708e39da417e51c854"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${datadir}/sounds/sf2
    install -m 0644 ${WORKDIR}/TimGM6mb.sf2 ${D}${datadir}/sounds/sf2/
}

inherit allarch
