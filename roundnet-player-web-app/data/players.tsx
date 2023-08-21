export const players:Map<string, playerInterface> = new Map([
  ["1", {
      id: "1",
      name: 'Rafi Moldovsky',
  }],
  ["2", {
    id: "2",
    name: 'Kyan Kornfeld',
  }],
  ["3", {
    id: "3",
    name: 'William Foote',
  }], 
  ["4", {
    id: "4",
    name: 'Arrow Griner',
  }], 
]);

export interface playerInterface {
  id: string;
  name: string;
}

export function isAPlayer(obj: any): obj is playerInterface {
  if (typeof obj === 'undefined'){return false};
  return 'id' in obj && 'name' in obj;
}