FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://sysctl.conf"

do_configure_prepend() {
	export ac_cv_func_malloc_0_nonnull=yes
	export ac_cv_func_realloc_0_nonnull=yes
}
