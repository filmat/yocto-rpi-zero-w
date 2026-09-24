do_install:append() {
    # Upstream already defines the wlan0 stanza (including wpa-conf) but does
    # not bring the interface up at boot. Add only the missing "auto" line
    # instead of appending a second, duplicate wlan0 stanza.
    if ! grep -q '^auto wlan0' ${D}${sysconfdir}/network/interfaces; then
        sed -i 's/^iface wlan0 inet dhcp/auto wlan0\n&/' ${D}${sysconfdir}/network/interfaces
    fi
    grep -q '^auto wlan0' ${D}${sysconfdir}/network/interfaces || \
        bbfatal "init-ifupdown: could not add 'auto wlan0' to interfaces"
}
