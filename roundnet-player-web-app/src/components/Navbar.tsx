import Link from "next/link"

export default function Navbar() {
  return (
    <div className="flex justify-around my-10 text-lg font-semibold">
      <Link href={"/"}>
        <button>
          Home
        </button>
      </Link>
      <Link href={"/tournaments"}>
        <button>
          Tournaments
        </button>
      </Link>
      <Link href={"/players"}>
        <button>
          Players
        </button>
      </Link>
    </div>
  )
}