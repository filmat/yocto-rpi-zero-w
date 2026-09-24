do_install:append() {
    cat >> ${D}${sysconfdir}/network/interfaces <<'IFACES'

auto wlan0
iface wlan0 inet dhcp
    wpa-conf /etc/wpa_supplicant.conf
IFACES
}
