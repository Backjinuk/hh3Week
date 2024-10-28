import http from 'k6/http';

export default function () {
    let url = 'https://httpbin.test.k6.io/post';
    let response = http.post(url, 'Hello world!');
}
