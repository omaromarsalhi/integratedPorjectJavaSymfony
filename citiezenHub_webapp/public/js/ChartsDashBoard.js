(function (factory) {
    typeof define === 'function' && define.amd ? define(factory) :
        factory();
})((function () {
    'use strict';

    const {merge: merge} = window._;
    const echartSetOption = (e, t, o, r) => {
        const {breakpoints: a, resize: n} = window.phoenix.utils, s = t => {
            Object.keys(t).forEach((o => {
                window.innerWidth > a[o] && e.setOption(t[o]);
            }));
        }, i = document.body;
        e.setOption(merge(o(), t));
        const c = document.querySelector(".navbar-vertical-toggle");
        c && c.addEventListener("navbar.vertical.toggle", (() => {
            e.resize(), r && s(r);
        })), n((() => {
            e.resize(), r && s(r);
        })), r && s(r), i.addEventListener("clickControl", (({detail: {control: r}}) => {
            "phoenixTheme" === r && e.setOption(window._.merge(o(), t));
        }));
    };
    const echartTabs = document.querySelectorAll("[data-tab-has-echarts]");
    echartTabs && echartTabs.forEach((e => {
        e.addEventListener("shown.bs.tab", (e => {
            const t = e.target, {hash: o} = t, r = o || t.dataset.bsTarget,
                a = document.getElementById(r.substring(1))?.querySelector("[data-echart-tab]");
            a && window.echarts.init(a).resize();
        }));
    }));
    const tooltipFormatter = (e, t = "MMM DD") => {
        let o = "";
        return e.forEach((e => {
            o += `<div class='ms-1'>\n        <h6 class="text-body-tertiary"><span class="fas fa-circle me-1 fs-10" style="color:${e.borderColor ? e.borderColor : e.color}"></span>\n          ${e.seriesName} : ${"object" == typeof e.value ? e.value[1] : e.value}\n        </h6>\n      </div>`;
        })), `<div>\n            <p class='mb-2 text-body-tertiary'>\n              ${window.dayjs(e[0].axisValue).isValid() ? window.dayjs(e[0].axisValue).format(t) : e[0].axisValue}\n            </p>\n            ${o}\n          </div>`
    };
    const handleTooltipPosition = ([e, , t, , o]) => {
        if (window.innerWidth <= 540) {
            const r = t.offsetHeight, a = {top: e[1] - r - 20};
            return a[e[0] < o.viewSize[0] / 2 ? "left" : "right"] = 5, a
        }
        return null
    };


    //charts-----------------------------------------------------------------------------------------------------------------------------------------
    const basicLineChartInit = () => {
        const {getColor: e, getData: r} = window.phoenix.utils,
            o = document.querySelector(".echart-line-chart-example"),
            t = getLastSevenDays(),
            i = usersCount,
            a = e => `\n    <div>\n        <h6 class="fs-9 text-body-tertiary mb-0">\n          <span class="fas fa-circle me-1" style='color:${e[0].borderColor}'></span>\n          ${e[0].name} : ${e[0].value}\n        </h6>\n    </div>\n    `;
        if (o) {
            const n = r(o, "echarts"), l = window.echarts.init(o);
            echartSetOption(l, n, (() => ({
                tooltip: {
                    trigger: "axis",
                    padding: [7, 10],
                    backgroundColor: e("body-highlight-bg"),
                    borderColor: e("border-color"),
                    textStyle: {color: e("light-text-emphasis")},
                    borderWidth: 1,
                    transitionDuration: 0,
                    formatter: a,
                    axisPointer: {type: "none"}
                },
                xAxis: {
                    type: "category",
                    data: t,
                    boundaryGap: !1,
                    axisLine: {lineStyle: {color: e("tertiary-bg")}},
                    axisTick: {show: !1},
                    axisLabel: {color: e("quaternary-color"), formatter: e => e.substring(0, 3), margin: 15},
                    splitLine: {show: !1}
                },
                yAxis: {
                    type: "value",
                    splitLine: {lineStyle: {type: "dashed", color: e("secondary-bg")}},
                    boundaryGap: !1,
                    axisLabel: {show: !0, color: e("quaternary-color"), margin: 15},
                    axisTick: {show: !1},
                    axisLine: {show: !1},
                    min: 0,
                    max: usersCount[usersCount.length - 1] + 8
                },
                series: [{
                    type: "line",
                    data: i,
                    itemStyle: {color: e("body-highlight-bg"), borderColor: e("primary"), borderWidth: 2},
                    lineStyle: {color: e("primary")},
                    showSymbol: !1,
                    symbol: "circle",
                    symbolSize: 10,
                    smooth: !1,
                    hoverAnimation: !0
                }],
                grid: {right: "3%", left: "10%", bottom: "10%", top: "5%"}
            })));
        }
    };

    const pieChartInit = (data) => {
        const {getColor: e, getData: t, rgbaColor: o} = window.phoenix.utils,
            r = document.querySelector(".echart-pie-chart-example");
        if (r) {
            const i = t(r, "echarts"), a = window.echarts.init(r);
            echartSetOption(a, i, (() => ({
                legend: {left: "left", textStyle: {color: e("tertiary-color")}},
                series: [{
                    type: "pie",
                    radius: window.innerWidth < 530 ? "45%" : "60%",
                    label: {color: e("tertiary-color")},
                    center: ["50%", "55%"],
                    data: data,
                    emphasis: {itemStyle: {shadowBlur: 10, shadowOffsetX: 0, shadowColor: o(e("tertiary-color"), .5)}}
                }],
                tooltip: {
                    trigger: "item",
                    padding: [7, 10],
                    backgroundColor: e("body-highlight-bg"),
                    borderColor: e("border-color"),
                    textStyle: {color: e("light-text-emphasis")},
                    borderWidth: 1,
                    transitionDuration: 0,
                    axisPointer: {type: "none"}
                }
            })), {xs: {series: [{radius: "45%"}]}, sm: {series: [{radius: "60%"}]}});
        }
    };

    const basicAreaLineChartInit = () => {
        console.log(usersCount);
        const {getColor: r, getData: o, rgbaColor: e} = window.phoenix.utils,
            t = document.querySelector(".echart-area-line-chart-example"),
            i = getLastSevenDays(),
            a = usersCount;
        if (t) {
            const l = o(t, "echarts"), n = window.echarts.init(t);
            echartSetOption(n, l, (() => ({
                tooltip: {
                    trigger: "axis",
                    padding: [7, 10],
                    backgroundColor: r("body-highlight-bg"),
                    borderColor: r("border-color"),
                    textStyle: {color: r("light-text-emphasis")},
                    borderWidth: 1,
                    formatter: r => (r => `\n    <div>\n        <h6 class="fs-9 text-body-tertiary mb-0">\n          <span class="fas fa-circle me-1" style='color:${r[0].borderColor}'></span>\n          ${r[0].name} : ${r[0].value}\n        </h6>\n    </div>\n    `)(r),
                    transitionDuration: 0,
                    axisPointer: {type: "none"}
                },
                xAxis: {
                    type: "category",
                    data: i,
                    boundaryGap: !1,
                    axisLine: {lineStyle: {color: r("tertiary-bg"), type: "solid"}},
                    axisTick: {show: !1},
                    axisLabel: {color: r("quaternary-color"), formatter: r => r.substring(0, 3), margin: 15},
                    splitLine: {show: !1}
                },
                yAxis: {
                    type: "value",
                    splitLine: {lineStyle: {color: r("secondary-bg")}},
                    boundaryGap: !1,
                    axisLabel: {show: !0, color: r("quaternary-color"), margin: 15},
                    axisTick: {show: !1},
                    axisLine: {show: !1},
                    min: 0,
                    max: usersCount[usersCount.length - 1] + 8
                },
                series: [{
                    type: "line",
                    data: a,
                    itemStyle: {color: r("body-highlight-bg"), borderColor: r("primary"), borderWidth: 2},
                    lineStyle: {color: r("primary")},
                    showSymbol: !1,
                    symbolSize: 10,
                    symbol: "circle",
                    smooth: !1,
                    hoverAnimation: !0,
                    areaStyle: {
                        color: {
                            type: "linear",
                            x: 0,
                            y: 0,
                            x2: 0,
                            y2: 1,
                            colorStops: [{offset: 0, color: e(r("primary"), .5)}, {
                                offset: 1,
                                color: e(r("primary"), 0)
                            }]
                        }
                    }
                }],
                grid: {right: "3%", left: "10%", bottom: "10%", top: "5%"}
            })));
        }
    };

    const seriesBarChartInit = (response) => {
        const {getColor: t, getData: e} = window.phoenix.utils,
            r = document.querySelector(".echart-series-bar-chart-example");
        if (r) {
            const o = e(r, "echarts"), i = window.echarts.init(r);
            echartSetOption(i, o, (() => ({
                color: [t("primary"), t("info")],
                tooltip: {
                    trigger: "axis",
                    axisPointer: {type: "shadow"},
                    padding: [7, 10],
                    backgroundColor: t("body-highlight-bg"),
                    borderColor: t("border-color"),
                    textStyle: {color: t("light-text-emphasis")},
                    borderWidth: 1,
                    transitionDuration: 0,
                    formatter: t => tooltipFormatter(t)
                },
                xAxis: {
                    type: "value",
                    axisLabel: {formatter: t => t, color: t("quaternary-color")},
                    axisLine: {show: !0, lineStyle: {color: t("tertiary-bg"), type: "solid"}},
                    splitLine: {lineStyle: {type: "dashed", color: t("secondary-bg")}}
                },
                yAxis: {
                    type: "category",
                    axisLine: {show: !0, lineStyle: {color: t("tertiary-bg"), type: "solid"}},
                    axisLabel: {color: t("quaternary-color")},
                    axisTick: {show: !1},
                    splitLine: {show: !1},
                    data: ["Electronics", "Vehicule"]
                },
                series: [{
                    name: "unverified",
                    type: "bar",
                    data: [response.Electronics.unverified, response.Vehicule.unverified],
                    itemStyle: {barBorderRadius: [0, 3, 3, 0]}
                }, {
                    name: "verified",
                    type: "bar",
                    data: [response.Electronics.verified, response.Vehicule.verified],
                    itemStyle: {barBorderRadius: [0, 3, 3, 0]}
                }],
                grid: {right: 15, left: "12%", bottom: "10%", top: 5}
            })));
        }
    };

    const stackedLineChartInit = () => {
        const {getColor: o, getData: e} = window.phoenix.utils,
            t = document.querySelector(".echart-stacked-line-chart-example"),
            r = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
        if (t) {
            const i = e(t, "echarts"), a = window.echarts.init(t);
            echartSetOption(a, i, (() => ({
                tooltip: {
                    trigger: "axis",
                    padding: [7, 10],
                    backgroundColor: o("body-highlight-bg"),
                    borderColor: o("border-color"),
                    textStyle: {color: o("light-text-emphasis")},
                    borderWidth: 1,
                    transitionDuration: 0,
                    axisPointer: {type: "none"},
                    position: (...o) => handleTooltipPosition(o),
                    formatter: o => tooltipFormatter(o)
                },
                xAxis: {
                    type: "category",
                    data: r,
                    boundaryGap: !1,
                    axisLine: {lineStyle: {color: o("tertiary-bg"), type: "solid"}},
                    axisTick: {show: !1},
                    axisLabel: {color: o("quaternary-color"), margin: 15, formatter: o => o.substring(0, 3)},
                    splitLine: {show: !1}
                },
                yAxis: {
                    type: "value",
                    splitLine: {lineStyle: {color: o("secondary-bg"), type: "dashed"}},
                    boundaryGap: !1,
                    axisLabel: {show: !0, color: o("quaternary-color"), margin: 15},
                    axisTick: {show: !1},
                    axisLine: {show: !1},

                },
                series: [{
                    name: "Matcha Latte",
                    type: "line",
                    symbolSize: 6,
                    itemStyle: {color: o("body-highlight-bg"), borderColor: o("info"), borderWidth: 2},
                    lineStyle: {color: o("info")},
                    symbol: "circle",
                    stack: "product",
                    data: [1, 0, 3, 4, 1, 0, 8]
                },],
                grid: {right: 10, left: 5, bottom: 5, top: 8, containLabel: !0}
            })));
        }
    };

    //functions-----------------------------------------------------------------------------------------------------------------------------------------

    var usersCount = [];
    var colors = ["#FF5733", "#C70039", "#900C3F", "#581845", "#FFC300", "#FF5733", "#DAF7A6", "#FFB347", "#FFCC33", "#FFD700", "#40E0D0", "#00CED1", "#4682B4", "#6495ED", "#7B68EE", "#6A5ACD", "#00FF7F", "#32CD32", "#ADFF2F", "#7FFF00", "#FF69B4", "#FF1493", "#FF6347", "#FF4500"];

    function getLastSevenDays() {
        var daysOfWeek = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        var result = [];
        for (var i = 6; i >= 0; i--) {
            var d = new Date();
            d.setDate(d.getDate() - i);
            result.push(daysOfWeek[d.getDay()]);
        }
        return result;
    }

    function getUsers() {
        $.ajax({
            url: '/userCountLastSixDays',
            type: 'GET',
            success: function (response) {
                usersCount = response.map(function (item) {
                    return item.compte_cree;
                });
                basicAreaLineChartInit();
            },
            error: function (xhr, status, error) {

                console.error(error);
            }
        });
    }

    function getGov() {
        $.ajax({
            url: '/GovrGet',
            type: 'GET',
            success: function (response) {
                var govnbr = [];
                console.log(response);
                for (var i = 0; i < response.length; i++) {
                    govnbr.push({
                        value: response[i].numberMunicipalities,
                        name: response[i].govenmentName,
                        itemStyle: {color: colors[i]}
                    });
                }
                pieChartInit(govnbr);
                console.log(govnbr);
            },
            error: function (xhr, status, error) {

                console.error(error);
            }
        });
    }

    function getProd() {
        $.ajax({
            url: '/product/statProd',
            type: 'GET',
            success: function (response) {
                console.log(response);
                seriesBarChartInit(response);
            },
            error: function (xhr, status, error) {

                console.error(error);
            }
        });
    }

    $(document).ready(function () {

        getUsers();

        pieChartInit();

        basicAreaLineChartInit();

        stackedLineChartInit();

        getGov();

        getProd();


    });

}));