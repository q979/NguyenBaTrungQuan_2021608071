/**
 * Initialize recommendation carousels
 */
$(document).ready(() => {
    // Initialize "For You" carousel
    if (typeof $(".furniture--4").owlCarousel === "function") {
        $(".furniture--4").owlCarousel({
            loop: false,
            margin: 0,
            nav: true,
            navText: ['<i class="zmdi zmdi-chevron-left"></i>', '<i class="zmdi zmdi-chevron-right"></i>'],
            dots: false,
            autoplay: false,
            responsive: {
                0: { items: 1 },
                576: { items: 2 },
                768: { items: 3 },
                992: { items: 4 },
                1920: { items: 4 },
            },
        })
    }

    // Initialize "Similar Products" carousel
    if (typeof $(".productcategory__slide--2").owlCarousel === "function") {
        $(".productcategory__slide--2").owlCarousel({
            loop: false,
            margin: 0,
            nav: true,
            navText: ['<i class="zmdi zmdi-chevron-left"></i>', '<i class="zmdi zmdi-chevron-right"></i>'],
            dots: false,
            autoplay: false,
            responsive: {
                0: { items: 1 },
                576: { items: 2 },
                768: { items: 3 },
                992: { items: 4 },
                1920: { items: 4 },
            },
        })
    }
})
