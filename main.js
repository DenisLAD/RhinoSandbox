let checkIp = "https://ifconfig.co/json";
let checkPort = "https://ifconfig.co/port/";

let summary = JSON.parse(httpRequest({
    method: "GET",
    url: checkIp,
    headers: {
        "Content-Type": "application/json;charset=utf-8"
    }
}).body);

console.log("Your IP is: " + summary.ip);

const portsToCheck = [80, 8080, 443, 25, 995, 9990, 453, 5000];

portsToCheck.map((port) => {
    let data = JSON.parse(httpRequest({
        method: "GET",
        url: checkPort + port,
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        }
    }).body);

    console.log("PORT " + port + " " + (data.reacheble ? "opened" : "closed"));
});

