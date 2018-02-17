import { Worker } from './../worker/worker';
export class Incident {
    id: number;
    category: string;
    severity: number;
    recordTime: Date;
    subject: Worker;
    happenTime: Date;
    location: string;
    title: string;
    description: string;
}
