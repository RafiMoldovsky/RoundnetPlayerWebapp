'use client';
import { useSearchParams } from 'next/navigation';
import { players, playerInterface, isAPlayer } from '../../../../data/players';

interface pageProps{
  params: {id: string}
}

export default function PlayerPage({params}: pageProps){
  const player = players.get(params.id);
  console.log(isAPlayer(player));
  if(!isAPlayer(player)) {
    return null;
  }
  return (
    <div>{player.id} {player.name}</div>
  )
}