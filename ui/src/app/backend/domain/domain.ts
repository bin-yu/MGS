export class Domain {
    id: number;
    name: string;
    label: string;
    children: Domain[];

    public static copy(copy): Domain {
        const d = new Domain();
        d.id = copy.id;
        d.name = copy.name;
        d.label = copy.label;
        return d;
    }
}
