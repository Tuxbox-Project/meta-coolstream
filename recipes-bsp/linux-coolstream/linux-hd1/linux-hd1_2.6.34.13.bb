require linux.inc

DEPENDS += "xz-native"
DEPENDS += "u-boot-mkimage-native"

DESCRIPTION = "Linux kernel for the Coolstream HD1 boxes"
COMPATIBLE_MACHINE = "coolstream-hd1"
PROVIDES = "virtual/kernel"
PR = "r1"

SRC_URI = "http://www.kernel.org/pub/linux/kernel/v2.6/longterm/v2.6.34/linux-2.6.34.13.tar.xz \
	file://0001-Coolstream-patches-for-linux-kernel-2.6.34.13.patch \
	file://0002-arch-arm-mach-nevis-irq.c-fix-gpio-irq-numbers.patch \
	file://0003-drivers-staging-rt2870-Kconfig-remove-Ralink-2870-30.patch \
	file://0004-Staging-rt2870sta-Add-more-device-IDs-from-vendor-dr.patch \
	file://0005-build.sh-uncomment-make-modules.patch \
	file://0006-config-2.6.34.13-enable-RT2870.patch \
	file://0007-drivers-net-arcvmac.c-update-to-latest-driver-versio.patch \
	file://0008-drivers-net-arcvmac.c-fix-oops-on-ifdown-ifup.patch \
	file://0009-arch-arm-mach-nevis-gpio.c-fix-warning.patch \
	file://0010-linux-fix-clkdev-support-in-older-kernels.-wrong-con.patch \
	file://0011-drivers-net-arcvmac.c-get-the-clock-from-the-clkdev..patch \
	file://0012-drivers-net-arcvmac.c-fix-warning.patch \
	file://0013-Cifs-version-1.64.patch \
	file://0014-drivers-net-arcvmac.c-fix-mask-to-check-for-spurious.patch \
	file://0015-cifs-fix-cifs-stable-patch-cifs-fix-oplock-break-han.patch \
	file://0016-config-2.6.34.13-enable-nfs-server-for-image-2.11.patch \
	file://0001-ARM-6329-1-wire-up-sys_accept4-on-ARM.patch \
	file://fix_kernel_build.patch \
	file://defconfig \
	file://dvb-fe-avl2108.fw \
"

S = "${WORKDIR}/linux-${PV}"
KV = "${PV}-nevis"

# NOTE: For now we pull in the default config from the RPi kernel GIT tree.
KERNEL_DEFCONFIG = "defconfig"

# CMDLINE for Coolstream is set by U-Boot.
CMDLINE_coolstream = ""

UDEV_GE_141 ?= "1"

kernel_do_configure_prepend() {
	cd ${S} && make mrproper
	cp '${WORKDIR}/defconfig' '${S}/.config'
	cp '${WORKDIR}/dvb-fe-avl2108.fw' '${S}/firmware/dvb-fe-avl2108.fw'
}

kernel_do_install_prepend() {
	install -d ${D}${localstatedir}/update ${D}/lib/firmware
	uboot-mkimage -A arm -O linux -T kernel -a 0x048000 -e 0x048000 -C none \
		-n "CS HD1 Kernel ${PV} (zImage)" -d arch/arm/boot/zImage zImage.img
	# hack: we replace the zImage with the U-Boot image...
	mv arch/arm/boot/zImage arch/arm/boot/zImage.orig
	mv zImage.img arch/arm/boot/zImage
	if [ ${INCLUDE_KERNEL} = "yes" ];then
	install arch/arm/boot/zImage ${D}${localstatedir}/update/uImage
	fi
}

kernel_do_install_append() {
	# self-built dvb-core does not work, need to use the binary-only illegal one :-(
	rm    ${D}/lib/modules/${KV}/kernel/drivers/media/dvb/dvb-core/dvb-core.ko
	rmdir ${D}/lib/modules/${KV}/kernel/drivers/media/dvb/dvb-core
	rmdir ${D}/lib/modules/${KV}/kernel/drivers/media/dvb
	rm -f ${D}${localstatedir}/update/zImage-${KERNEL_VERSION}
	rm -rf ${WORKDIR}/image/boot
}
	
FILES_kernel-image = "/var/update"

SRC_URI[md5sum] = "ec72935604c58d26b044e0cb2a496a3b"
SRC_URI[sha256sum] = "cb50a9ec2e24fb0d02eb4983e5c7bc61725cdcf212813a79121a0bb12ac0b4ce"

