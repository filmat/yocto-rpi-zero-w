require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL:append = " kernel-modules wpa-supplicant wpa-supplicant-passphrase linux-firmware-rpidistro-bcm43430 usbutils alsa-utils"
IMAGE_FEATURES += "ssh-server-dropbear"
